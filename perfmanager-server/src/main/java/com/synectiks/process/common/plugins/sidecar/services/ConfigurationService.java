/*
 * */
package com.synectiks.process.common.plugins.sidecar.services;

import com.mongodb.BasicDBObject;
import com.synectiks.process.common.plugins.sidecar.rest.models.Configuration;
import com.synectiks.process.common.plugins.sidecar.rest.models.ConfigurationVariable;
import com.synectiks.process.common.plugins.sidecar.rest.models.Sidecar;
import com.synectiks.process.common.plugins.sidecar.template.RenderTemplateException;
import com.synectiks.process.common.plugins.sidecar.template.directives.IndentTemplateDirective;
import com.synectiks.process.common.plugins.sidecar.template.loader.MongoDbTemplateLoader;
import com.synectiks.process.server.bindings.providers.MongoJackObjectMapperProvider;
import com.synectiks.process.server.database.MongoConnection;
import com.synectiks.process.server.database.PaginatedDbService;
import com.synectiks.process.server.database.PaginatedList;
import com.synectiks.process.server.search.SearchQuery;

import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.StringTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import org.mongojack.DBQuery;
import org.mongojack.DBSort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang.CharEncoding.UTF_8;

@Singleton
public class ConfigurationService extends PaginatedDbService<Configuration> {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationService.class);
    private static final freemarker.template.Configuration templateConfiguration =
            new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_28);
    private static final StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
    private ConfigurationVariableService configurationVariableService;

    private static final String COLLECTION_NAME = "sidecar_configurations";

    @Inject
    public ConfigurationService(MongoConnection mongoConnection,
                                MongoJackObjectMapperProvider mapper,
                                ConfigurationVariableService configurationVariableService) {
        super(mongoConnection, mapper, Configuration.class, COLLECTION_NAME);
        MongoDbTemplateLoader mongoDbTemplateLoader = new MongoDbTemplateLoader(db);
        MultiTemplateLoader multiTemplateLoader = new MultiTemplateLoader(new TemplateLoader[] {
                mongoDbTemplateLoader,
                stringTemplateLoader });
        templateConfiguration.setTemplateLoader(multiTemplateLoader);
        templateConfiguration.setSharedVariable("indent", new IndentTemplateDirective());
        templateConfiguration.setDefaultEncoding(UTF_8);
        templateConfiguration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        templateConfiguration.setLogTemplateExceptions(false);
        this.configurationVariableService = configurationVariableService;
    }

    public Configuration find(String id) {
        return db.findOne(DBQuery.is("_id", id));
    }

    public Configuration findByName(String name) {
        return db.findOne(DBQuery.is(Configuration.FIELD_NAME, name));
    }

    public long count() {
        return db.count();
    }

    public List<Configuration> all() {
        try (final Stream<Configuration> collectorConfigurationStream = streamAll()) {
            return collectorConfigurationStream.collect(Collectors.toList());
        }
    }

    public PaginatedList<Configuration> findPaginated(SearchQuery searchQuery, int page, int perPage, String sortField, String order) {
        final DBQuery.Query dbQuery = searchQuery.toDBQuery();
        final DBSort.SortBuilder sortBuilder = getSortBuilder(order, sortField);
        return findPaginatedWithQueryAndSort(dbQuery, sortBuilder, page, perPage);
    }

    public List<Configuration> findByQuery(DBQuery.Query query) {
        try (final Stream<Configuration> collectorConfigurationStream = streamQuery(query)) {
            return collectorConfigurationStream.collect(Collectors.toList());
        }
    }

    public List<Configuration> findByConfigurationVariable(ConfigurationVariable configurationVariable) {
        final DBQuery.Query query = DBQuery.regex(Configuration.FIELD_TEMPLATE, Pattern.compile(Pattern.quote(configurationVariable.fullName())));
        return findByQuery(query);
    }

    public void replaceVariableNames(String oldName, String newName) {
        final DBQuery.Query query = DBQuery.regex(Configuration.FIELD_TEMPLATE, Pattern.compile(Pattern.quote(oldName)));
        List<Configuration> configurations = findByQuery(query);
        for (Configuration config : configurations) {
            final String newTemplate = config.template().replace(oldName, newName);
            db.findAndModify(DBQuery.is("_id", config.id()), new BasicDBObject(),
                    new BasicDBObject(), false, config.toBuilder().template(newTemplate).build(), true, true);
        }
    }

    @Override
    public Configuration save(Configuration configuration) {
        return db.findAndModify(DBQuery.is("_id", configuration.id()), new BasicDBObject(),
                new BasicDBObject(), false, configuration, true, true);
    }

    public Configuration copyConfiguration(String id, String name) {
        Configuration configuration = find(id);
        return Configuration.create(configuration.collectorId(), name, configuration.color(), configuration.template());
    }

    public Configuration fromRequest(Configuration request) {
        return Configuration.create(
                request.collectorId(),
                request.name(),
                request.color(),
                request.template());
    }

    public Configuration fromRequest(String id, Configuration request) {
        return Configuration.create(
                id,
                request.collectorId(),
                request.name(),
                request.color(),
                request.template());
    }

    public Configuration renderConfigurationForCollector(Sidecar sidecar, Configuration configuration) throws RenderTemplateException {
        Map<String, Object> context = new HashMap<>();

        context.put("nodeId", sidecar.nodeId());
        context.put("nodeName", sidecar.nodeName());
        context.put("sidecarVersion", sidecar.sidecarVersion());
        context.put("operatingSystem", sidecar.nodeDetails().operatingSystem());

        return Configuration.create(
                configuration.id(),
                configuration.collectorId(),
                configuration.name(),
                configuration.color(),
                renderTemplate(configuration.id(), context)
        );
    }

    public String renderPreview(String template) throws RenderTemplateException {
        Map<String, Object> context = new HashMap<>();
        context.put("nodeId", "<node id>");
        context.put("nodeName", "<node name>");
        context.put("sidecarVersion", "<version>");
        context.put("operatingSystem", "<operating system>");

        String previewName = UUID.randomUUID().toString();
        stringTemplateLoader.putTemplate(previewName, template);
        String result = renderTemplate(previewName, context);
        stringTemplateLoader.removeTemplate(previewName);
        try {
            templateConfiguration.removeTemplateFromCache(previewName);
        } catch (IOException e) {
            LOG.debug("Couldn't remove temporary template from cache: " + e.getMessage());
        }

        return result;
    }

    private String renderTemplate(String configurationId, Map<String, Object> sidecarContext) throws RenderTemplateException {
        Writer writer = new StringWriter();
        String template;

        final Map<String, Object> context = new HashMap<>();
        context.put("sidecar", sidecarContext);

        final Map<String, Object> userContext =
                configurationVariableService.all().stream().collect(Collectors.toMap(ConfigurationVariable::name, ConfigurationVariable::content));
        context.put(ConfigurationVariable.VARIABLE_PREFIX, userContext);

        try {
            Template compiledTemplate = templateConfiguration.getTemplate(configurationId);
            compiledTemplate.process(context, writer);
        } catch (TemplateException e) {
            LOG.error("Failed to render template: " + e.getMessageWithoutStackTop());
            throw new RenderTemplateException(e.getFTLInstructionStack(), e);
        } catch (IOException e) {
            LOG.error("Failed to render template: ", e);
            throw new RenderTemplateException(e.getMessage(), e);
        }

        template = writer.toString();
        return template.endsWith("\n") ? template : template + "\n";
    }
}
