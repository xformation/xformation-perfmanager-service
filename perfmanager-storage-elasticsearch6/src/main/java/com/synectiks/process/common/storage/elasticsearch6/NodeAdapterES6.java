/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch6;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Ping;

import com.synectiks.process.common.storage.elasticsearch6.jest.JestUtils;
import com.synectiks.process.server.indexer.cluster.NodeAdapter;

import javax.inject.Inject;
import java.util.Optional;

public class NodeAdapterES6 implements NodeAdapter {
    private final JestClient jestClient;

    @Inject
    public NodeAdapterES6(JestClient jestClient) {
        this.jestClient = jestClient;
    }

    @Override
    public Optional<String> version() {
        final Ping request = new Ping.Builder().build();
        final JestResult jestResult = JestUtils.execute(jestClient, request, () -> "Unable to retrieve Elasticsearch version");
        return Optional.ofNullable(jestResult.getJsonObject().path("version").path("number").asText(null));
    }
}
