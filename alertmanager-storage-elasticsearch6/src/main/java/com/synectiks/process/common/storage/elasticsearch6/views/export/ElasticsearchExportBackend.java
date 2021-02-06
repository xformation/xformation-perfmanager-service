/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch6.views.export;

import com.google.inject.name.Named;
import com.synectiks.process.common.plugins.views.search.elasticsearch.ElasticsearchQueryString;
import com.synectiks.process.common.plugins.views.search.elasticsearch.IndexLookup;
import com.synectiks.process.common.plugins.views.search.export.ExportBackend;
import com.synectiks.process.common.plugins.views.search.export.ExportMessagesCommand;
import com.synectiks.process.common.plugins.views.search.export.SimpleMessage;
import com.synectiks.process.common.plugins.views.search.export.SimpleMessageChunk;
import com.synectiks.process.common.storage.elasticsearch6.TimeRangeQueryFactory;
import com.synectiks.process.server.indexer.IndexMapping;
import com.synectiks.process.server.plugin.Message;

import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.graylog.shaded.elasticsearch5.org.elasticsearch.index.query.QueryBuilder;
import org.graylog.shaded.elasticsearch5.org.elasticsearch.index.query.TermsQueryBuilder;
import org.graylog.shaded.elasticsearch5.org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import static com.synectiks.process.server.plugin.Tools.ES_DATE_FORMAT_FORMATTER;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toCollection;
import static org.graylog.shaded.elasticsearch5.org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.graylog.shaded.elasticsearch5.org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.graylog.shaded.elasticsearch5.org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.graylog.shaded.elasticsearch5.org.elasticsearch.index.query.QueryBuilders.termsQuery;

@SuppressWarnings("rawtypes")
public class ElasticsearchExportBackend implements ExportBackend {
    private static final Logger LOG = LoggerFactory.getLogger(ElasticsearchExportBackend.class);

    private final IndexLookup indexLookup;
    private final RequestStrategy requestStrategy;
    private final boolean allowLeadingWildcard;

    @Inject
    public ElasticsearchExportBackend(IndexLookup indexLookup, RequestStrategy requestStrategy, @Named("allow_leading_wildcard_searches") boolean allowLeadingWildcard) {
        this.indexLookup = indexLookup;
        this.requestStrategy = requestStrategy;
        this.allowLeadingWildcard = allowLeadingWildcard;
    }

    @Override
    public void run(ExportMessagesCommand command, Consumer<SimpleMessageChunk> chunkCollector) {
        boolean isFirstChunk = true;
        int totalCount = 0;

        while (true) {
            List<SearchResult.Hit<Map, Void>> hits = search(command);

            if (hits.isEmpty()) {
                return;
            }

            boolean success = publishChunk(chunkCollector, hits, command.fieldsInOrder(), isFirstChunk);
            if (!success) {
                return;
            }

            totalCount += hits.size();
            if (command.limit().isPresent() && totalCount >= command.limit().getAsInt()) {
                LOG.info("Limit of {} reached. Stopping message retrieval.", command.limit().getAsInt());
                return;
            }

            isFirstChunk = false;
        }
    }

    private List<SearchResult.Hit<Map, Void>> search(ExportMessagesCommand command) {
        Search.Builder search = prepareSearchRequest(command);

        return requestStrategy.nextChunk(search, command);
    }

    private Search.Builder prepareSearchRequest(ExportMessagesCommand command) {
        SearchSourceBuilder ssb = searchSourceBuilderFrom(command);

        Set<String> indices = indicesFor(command);
        return new Search.Builder(ssb.toString())
                .addType(IndexMapping.TYPE_MESSAGE)
                .allowNoIndices(false)
                .ignoreUnavailable(false)
                .addIndex(indices);
    }

    private SearchSourceBuilder searchSourceBuilderFrom(ExportMessagesCommand command) {
        QueryBuilder query = queryFrom(command);

        SearchSourceBuilder ssb = new SearchSourceBuilder()
                .query(query)
                .size(command.chunkSize());

        return requestStrategy.configure(ssb);
    }

    private QueryBuilder queryFrom(ExportMessagesCommand command) {
        return boolQuery()
                .filter(queryStringFilter(command))
                .filter(timestampFilter(command))
                .filter(streamsFilter(command));
    }

    private QueryBuilder queryStringFilter(ExportMessagesCommand command) {
        ElasticsearchQueryString backendQuery = command.queryString();
        return backendQuery.isEmpty() ?
                matchAllQuery() :
                queryStringQuery(backendQuery.queryString()).allowLeadingWildcard(allowLeadingWildcard);
    }

    private QueryBuilder timestampFilter(ExportMessagesCommand command) {
        return requireNonNull(TimeRangeQueryFactory.create(command.timeRange()));
    }

    private TermsQueryBuilder streamsFilter(ExportMessagesCommand command) {
        Set<String> streams = requestStrategy.removeUnsupportedStreams(command.streams());
        return termsQuery(Message.FIELD_STREAMS, streams);
    }

    private Set<String> indicesFor(ExportMessagesCommand command) {
        return indexLookup.indexNamesForStreamsInTimeRange(command.streams(), command.timeRange());
    }

    private boolean publishChunk(Consumer<SimpleMessageChunk> chunkCollector, List<SearchResult.Hit<Map, Void>> hits, LinkedHashSet<String> desiredFieldsInOrder, boolean isFirstChunk) {
        SimpleMessageChunk chunk = chunkFrom(hits, desiredFieldsInOrder, isFirstChunk);

        try {
            chunkCollector.accept(chunk);
            return true;
        } catch (Exception e) {
            LOG.warn("Chunk publishing threw exception. Stopping search after queries", e);
            return false;
        }
    }

    private SimpleMessageChunk chunkFrom(List<SearchResult.Hit<Map, Void>> hits, LinkedHashSet<String> desiredFieldsInOrder, boolean isFirstChunk) {
        LinkedHashSet<SimpleMessage> messages = messagesFrom(hits);

        return SimpleMessageChunk.builder()
                .fieldsInOrder(desiredFieldsInOrder)
                .messages(messages)
                .isFirstChunk(isFirstChunk)
                .build();
    }

    private LinkedHashSet<SimpleMessage> messagesFrom(List<SearchResult.Hit<Map, Void>> hits) {
        return hits.stream()
                .map(h -> buildHitWithAllFields(h.source, h.index))
                .collect(toCollection(LinkedHashSet::new));
    }

    private SimpleMessage buildHitWithAllFields(Map source, String index) {
        LinkedHashMap<String, Object> fields = new LinkedHashMap<>();

        for (Object key : source.keySet()) {
            String name = (String) key;
            Object value = valueFrom(source, name);
            fields.put(name, value);
        }

        // _id is needed, because the old decorators implementation relies on it
        fields.put("_id", UUID.randomUUID().toString());

        return SimpleMessage.from(index, fields);
    }

    private Object valueFrom(Map source, String name) {
        if (name.equals(Message.FIELD_TIMESTAMP)) {
            return fixTimestampFormat(source.get(Message.FIELD_TIMESTAMP));
        }
        return source.get(name);
    }

    private Object fixTimestampFormat(Object rawTimestamp) {
        try {
            return ES_DATE_FORMAT_FORMATTER.parseDateTime(String.valueOf(rawTimestamp)).toString();
        } catch (IllegalArgumentException e) {
            LOG.warn("Could not parse timestamp {}", rawTimestamp, e);
            return rawTimestamp;
        }
    }
}