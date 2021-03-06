/*
 * */
package com.synectiks.process.server.shared.rest.resources.documentation;

import com.floreysoft.jmte.Engine;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import com.synectiks.process.server.configuration.HttpConfiguration;
import com.synectiks.process.server.rest.RestTools;
import com.synectiks.process.server.shared.rest.resources.RestResource;

import javax.activation.MimetypesFileTypeMap;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static java.util.Objects.requireNonNull;

@Path("/api-browser")
public class DocumentationBrowserResource extends RestResource {
    private final MimetypesFileTypeMap mimeTypes;
    private final HttpConfiguration httpConfiguration;

    private final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
    private final Engine templateEngine;

    @Inject
    public DocumentationBrowserResource(MimetypesFileTypeMap mimeTypes, HttpConfiguration httpConfiguration, Engine templateEngine) {
        this.mimeTypes = requireNonNull(mimeTypes, "mimeTypes");
        this.httpConfiguration = requireNonNull(httpConfiguration, "httpConfiguration");
        this.templateEngine = requireNonNull(templateEngine, "templateEngine");
    }

    @GET
    public Response root(@Context HttpHeaders httpHeaders) throws IOException {
        final String index = index(httpHeaders);
        return Response.ok(index, MediaType.TEXT_HTML_TYPE)
                .header(HttpHeaders.CONTENT_LENGTH, index.length())
                .build();
    }

    // Serve Swagger for a specific node, using HttpPublishUri
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("index.html")
    public String index(@Context HttpHeaders httpHeaders) throws IOException {
        final URL templateUrl = this.getClass().getResource("/swagger/index.html.template");
        final String template = Resources.toString(templateUrl, StandardCharsets.UTF_8);
        final Map<String, Object> model = ImmutableMap.of(
                "baseUri", httpConfiguration.getHttpPublishUri().resolve(HttpConfiguration.PATH_API).toString(),
                "globalModePath", "",
                "globalUriMarker", "",
                "showWarning", "");
        return templateEngine.transform(template, model);
    }

    // Serve Swagger in cluster global mode, using HttpExternalUri
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/global/index.html")
    public String allIndex(@Context HttpHeaders httpHeaders) throws IOException {
        final URL templateUrl = this.getClass().getResource("/swagger/index.html.template");
        final String template = Resources.toString(templateUrl, StandardCharsets.UTF_8);
        final Map<String, Object> model = ImmutableMap.of(
                "baseUri", RestTools.buildExternalUri(httpHeaders.getRequestHeaders(), httpConfiguration.getHttpExternalUri()).resolve(HttpConfiguration.PATH_API).toString(),
                "globalModePath", "global/index.html",
                "globalUriMarker", "/global",
                "showWarning", "true");
        return templateEngine.transform(template, model);
    }

    @GET
    @Path("/{route: .*}")
    public Response asset(@PathParam("route") String route) throws IOException {
        // Remove path globalModePath before we serve swagger resources
        if (route.startsWith("global/")) {
            route = route.replaceFirst("global/", "");
        }

        // Trying to prevent directory traversal
        if (route.contains("..")) {
            throw new BadRequestException("Not allowed to access parent directory");
        }
        final URL resource = classLoader.getResource("swagger/" + route);
        if (null != resource) {
            try {
                final byte[] resourceBytes = Resources.toByteArray(resource);

                return Response.ok(resourceBytes, mimeTypes.getContentType(route))
                        .header("Content-Length", resourceBytes.length)
                        .build();
            } catch (IOException e) {
                throw new NotFoundException("Couldn't load " + resource, e);
            }
        } else {
            throw new NotFoundException("Couldn't find " + route);
        }
    }
}
