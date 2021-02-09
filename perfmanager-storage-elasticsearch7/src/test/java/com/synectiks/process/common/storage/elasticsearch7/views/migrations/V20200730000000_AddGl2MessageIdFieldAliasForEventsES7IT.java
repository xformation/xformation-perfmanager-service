/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch7.views.migrations;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.graylog.shaded.elasticsearch7.org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.graylog.shaded.elasticsearch7.org.elasticsearch.client.indices.GetIndexRequest;
import org.graylog.shaded.elasticsearch7.org.elasticsearch.client.indices.GetIndexResponse;
import org.graylog.shaded.elasticsearch7.org.elasticsearch.client.indices.PutMappingRequest;
import org.graylog.shaded.elasticsearch7.org.elasticsearch.cluster.metadata.MappingMetadata;

import com.synectiks.process.common.storage.elasticsearch7.testing.ElasticsearchInstanceES7;
import com.synectiks.process.common.storage.elasticsearch7.views.migrations.V20200730000000_AddGl2MessageIdFieldAliasForEventsES7;
import com.synectiks.process.common.testing.elasticsearch.ElasticsearchBaseTest;
import com.synectiks.process.common.testing.elasticsearch.ElasticsearchInstance;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static com.synectiks.process.server.plugin.Message.FIELD_XFPERF_MESSAGE_ID;


public class V20200730000000_AddGl2MessageIdFieldAliasForEventsES7IT extends ElasticsearchBaseTest {

    @Rule
    public final ElasticsearchInstanceES7 elasticsearch = ElasticsearchInstanceES7.create();

    @Override
    protected ElasticsearchInstance elasticsearch() {
        return this.elasticsearch;
    }

    private V20200730000000_AddGl2MessageIdFieldAliasForEventsES7 sut;

    @Before
    public void setUp() {
        sut = new V20200730000000_AddGl2MessageIdFieldAliasForEventsES7(elasticsearch.elasticsearchClient());
    }

    @Test
    public void addsFieldAliasForMatchingIndices() {
        createIndicesWithIdMapping("aaa_0", "aaa_1", "bbb_0");

        sut.addXfPerfMessageIdFieldAlias(ImmutableSet.of("aaa", "bbb"));

        assertFieldAliasIsPresentForIndices("aaa_0", "aaa_1", "bbb_0");
    }

    @Test
    public void doesNotAddFieldAliasForNonMatchingIndices() {
        createIndicesWithIdMapping("aaa_0", "aaa_1", "bbb_0");

        sut.addXfPerfMessageIdFieldAlias(ImmutableSet.of("bbb"));

        assertFieldAliasIsNotPresentForIndices("aaa_0", "aaa_1");
    }

    @Test
    public void addingFieldAliasesIsIdempotent() {
        createIndicesWithIdMapping("aaa_0", "bbb_0");

        sut.addXfPerfMessageIdFieldAlias(ImmutableSet.of("aaa"));
        sut.addXfPerfMessageIdFieldAlias(ImmutableSet.of("aaa"));

        assertFieldAliasIsPresentForIndices("aaa_0");
        assertFieldAliasIsNotPresentForIndices("bbb_0");
    }

    private void assertFieldAliasIsPresentForIndices(String... indices) {
        final GetIndexResponse response = getIndices(indices);

        for (String index : indices) {
            assertMappingsContainFieldAliasForIndex(response.getMappings(), index);
        }
    }

    private GetIndexResponse getIndices(String[] indices) {
        return elasticsearch.elasticsearchClient()
                .execute((c, opt) -> c.indices().get(new GetIndexRequest(indices), opt));
    }

    private void assertMappingsContainFieldAliasForIndex(Map<String, MappingMetadata> mappings, String index) {
        final Map<String, Object> properties = propertiesFrom(mappings, index);

        assertThat(properties).as(FIELD_XFPERF_MESSAGE_ID + " should be present in mapping for " + index)
                .containsKey(FIELD_XFPERF_MESSAGE_ID);
        assertThat(properties.get(FIELD_XFPERF_MESSAGE_ID)).as("field alias definition for " + index + " should be valid")
                .isEqualTo(V20200730000000_AddGl2MessageIdFieldAliasForEventsES7.aliasMapping());
    }

    private Map<String, Object> propertiesFrom(Map<String, MappingMetadata> mappings, String index) {
        //noinspection unchecked
        return (Map<String, Object>) mappings.get(index).getSourceAsMap().get("properties");
    }

    private void assertFieldAliasIsNotPresentForIndices(String... indices) {
        final GetIndexResponse response = getIndices(indices);

        for (String index : indices) {
            assertMappingsDontContainFieldAliasForIndex(response.getMappings(), index);
        }
    }

    private void assertMappingsDontContainFieldAliasForIndex(Map<String, MappingMetadata> mappings, String index) {
        final Map<String, Object> properties = propertiesFrom(mappings, index);

        assertThat(properties).as(FIELD_XFPERF_MESSAGE_ID + " should NOT be present in mapping for " + index)
                .doesNotContainKey(FIELD_XFPERF_MESSAGE_ID);
    }

    private void createIndicesWithIdMapping(String... indices) {
        for (String index : indices) {
            client().createIndex(index);
        }

        final PutMappingRequest putMappingRequest = new PutMappingRequest(indices).source(idMapping());
        final AcknowledgedResponse acknowledgedResponse = elasticsearch.elasticsearchClient()
                .execute((c, opt) -> c.indices().putMapping(putMappingRequest, opt));

        if (!acknowledgedResponse.isAcknowledged()) {
            throw new RuntimeException("Failed to add 'id' mapping for indices " + Arrays.toString(indices));
        }
    }

    private Map<String, Object> idMapping() {
        return map("properties", map("id", map("type", "keyword")));
    }

    private ImmutableMap<String, Object> map(String key, Object value) {
        return ImmutableMap.of(key, value);
    }
}
