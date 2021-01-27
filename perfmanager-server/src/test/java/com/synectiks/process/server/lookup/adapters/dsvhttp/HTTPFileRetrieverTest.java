/*
 * */
package com.synectiks.process.server.lookup.adapters.dsvhttp;

import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.synectiks.process.server.lookup.adapters.dsvhttp.HTTPFileRetriever;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class HTTPFileRetrieverTest {
    private MockWebServer server;

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        server = new MockWebServer();
    }

    @Test
    public void successfulRetrieve() throws Exception {
        this.server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("foobar"));
        server.start();

        final HTTPFileRetriever httpFileRetriever = new HTTPFileRetriever(new OkHttpClient());

        final Optional<String> body = httpFileRetriever.fetchFileIfNotModified(server.url("/").toString());
        final RecordedRequest request = server.takeRequest();

        assertThat(request).isNotNull();
        assertThat(request.getPath()).isEqualTo("/");

        assertThat(body).isNotNull()
                .isPresent()
                .contains("foobar");
    }

    @Test
    public void doNotRetrieveIfNotModified() throws Exception {
        this.server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("foobar")
                .setHeader("Last-Modified", "Fri, 18 Aug 2017 15:02:41 GMT"));
        this.server.enqueue(new MockResponse()
                .setResponseCode(304)
                .setHeader("Last-Modified", "Fri, 18 Aug 2017 15:02:41 GMT"));
        server.start();

        final HTTPFileRetriever httpFileRetriever = new HTTPFileRetriever(new OkHttpClient());

        final Optional<String> body = httpFileRetriever.fetchFileIfNotModified(server.url("/").toString());
        final RecordedRequest request = server.takeRequest();

        assertThat(request).isNotNull();
        assertThat(request.getPath()).isEqualTo("/");

        assertThat(body).isNotNull()
                .isPresent()
                .contains("foobar");

        final Optional<String> secondBody = httpFileRetriever.fetchFileIfNotModified(server.url("/").toString());
        final RecordedRequest secondRequest = server.takeRequest();

        assertThat(secondRequest).isNotNull();
        assertThat(secondRequest.getPath()).isEqualTo("/");
        assertThat(secondRequest.getHeader("If-Modified-Since")).isEqualTo("Fri, 18 Aug 2017 15:02:41 GMT");

        assertThat(secondBody).isNotNull()
                .isEmpty();
    }

    @Test
    public void fetchFileDoesNotSendIfModifiedSinceHeader() throws Exception {
        final MockResponse response = new MockResponse().setResponseCode(200)
                .setBody("foobar")
                .setHeader("Last-Modified", "Fri, 18 Aug 2017 15:02:41 GMT");
        this.server.enqueue(response);
        this.server.enqueue(response);
        server.start();

        final HTTPFileRetriever httpFileRetriever = new HTTPFileRetriever(new OkHttpClient());

        assertThat(httpFileRetriever.fetchFileIfNotModified(server.url("/").toString()))
                .isNotNull()
                .isPresent()
                .contains("foobar");
        assertThat(server.takeRequest()
                .getHeader("If-Modified-Since")).isNull();

        assertThat(httpFileRetriever.fetchFile(server.url("/").toString()))
                .isNotNull()
                .isPresent()
                .contains("foobar");
        assertThat(server.takeRequest()
                .getHeader("If-Modified-Since")).isNull();
    }

    @Test
    public void unsuccessfulRetrieve() throws Exception {
        this.server.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("Error!"));
        server.start();

        final HTTPFileRetriever httpFileRetriever = new HTTPFileRetriever(new OkHttpClient());

        expectedException.expect(IOException.class);
        expectedException.expectMessage("Request failed: Server Error");

        final Optional<String> ignored = httpFileRetriever.fetchFileIfNotModified(server.url("/").toString());
    }

    @After
    public void shutDown() throws IOException {
        if (server != null) {
            server.shutdown();
        }
    }
}
