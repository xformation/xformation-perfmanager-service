/*
 * */
package com.synectiks.process.server.cluster;

import com.mongodb.DBCollection;
import com.synectiks.process.common.testing.mongodb.MongoDBFixtures;
import com.synectiks.process.common.testing.mongodb.MongoDBInstance;
import com.synectiks.process.server.Configuration;
import com.synectiks.process.server.cluster.Node;
import com.synectiks.process.server.cluster.NodeService;
import com.synectiks.process.server.cluster.NodeServiceImpl;
import com.synectiks.process.server.plugin.Tools;
import com.synectiks.process.server.plugin.system.NodeId;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class NodeServiceImplTest {
    @Rule
    public final MongoDBInstance mongodb = MongoDBInstance.createForClass();

    private static final URI TRANSPORT_URI = URI.create("http://10.0.0.1:12900");
    private static final String LOCAL_CANONICAL_HOSTNAME = Tools.getLocalCanonicalHostname();
    private static final String NODE_ID = "28164cbe-4ad9-4c9c-a76e-088655aa7889";

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private Configuration configuration;
    @Mock
    private NodeId nodeId;

    private NodeService nodeService;

    @Before
    public void setUp() throws Exception {
        when(nodeId.toString()).thenReturn(NODE_ID);

        this.nodeService = new NodeServiceImpl(mongodb.mongoConnection(), configuration);
    }

    @Test
    @MongoDBFixtures("NodeServiceImplTest-empty.json")
    public void testRegisterServer() throws Exception {
        assertThat(nodeService.allActive())
                .describedAs("The collection should be empty")
                .isEmpty();

        nodeService.registerServer(nodeId.toString(), true, TRANSPORT_URI, LOCAL_CANONICAL_HOSTNAME);

        final Node node = nodeService.byNodeId(nodeId);

        assertThat(node).isNotNull();
        assertThat(node.getHostname()).isEqualTo(LOCAL_CANONICAL_HOSTNAME);
        assertThat(node.getTransportAddress()).isEqualTo(TRANSPORT_URI.toString());
        assertThat(node.isMaster()).isTrue();
    }

    @Test
    @MongoDBFixtures("NodeServiceImplTest-one-node.json")
    public void testRegisterServerWithExistingNode() throws Exception {
        final Node node1 = nodeService.byNodeId(nodeId);

        assertThat(node1.getNodeId())
                .describedAs("There should be one existing node")
                .isEqualTo(NODE_ID);

        nodeService.registerServer(nodeId.toString(), true, TRANSPORT_URI, LOCAL_CANONICAL_HOSTNAME);

        @SuppressWarnings("deprecation")
        final DBCollection collection = mongodb.mongoConnection().getDatabase().getCollection("nodes");

        assertThat(collection.count())
                .describedAs("There should only be one node")
                .isEqualTo(1);

        final Node node2 = nodeService.byNodeId(nodeId);

        assertThat(node2).isNotNull();
        assertThat(node2.getHostname()).isEqualTo(LOCAL_CANONICAL_HOSTNAME);
        assertThat(node2.getTransportAddress()).isEqualTo(TRANSPORT_URI.toString());
        assertThat(node2.isMaster()).isTrue();
    }
}
