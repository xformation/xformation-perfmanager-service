/*
 * */
package com.synectiks.process.server.rest.resources.cluster;

import com.codahale.metrics.annotation.Timed;
import com.synectiks.process.server.audit.AuditEventTypes;
import com.synectiks.process.server.audit.jersey.AuditEvent;
import com.synectiks.process.server.cluster.Node;
import com.synectiks.process.server.cluster.NodeNotFoundException;
import com.synectiks.process.server.cluster.NodeService;
import com.synectiks.process.server.rest.RemoteInterfaceProvider;
import com.synectiks.process.server.rest.resources.system.RemoteSystemShutdownResource;
import com.synectiks.process.server.shared.rest.resources.ProxiedResource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

import static javax.ws.rs.core.Response.Status.ACCEPTED;
import static javax.ws.rs.core.Response.Status.BAD_GATEWAY;

@RequiresAuthentication
@Api(value = "Cluster/Shutdown", description = "Shutdown gracefully nodes in cluster")
@Path("/cluster/{nodeId}/shutdown")
@Produces(MediaType.APPLICATION_JSON)
public class ClusterSystemShutdownResource extends ProxiedResource {
    private static final Logger LOG = LoggerFactory.getLogger(ClusterSystemShutdownResource.class);

    @Inject
    public ClusterSystemShutdownResource(NodeService nodeService,
                                         RemoteInterfaceProvider remoteInterfaceProvider,
                                         @Context HttpHeaders httpHeaders,
                                         @Named("proxiedRequestsExecutorService") ExecutorService executorService) throws NodeNotFoundException {
        super(httpHeaders, nodeService, remoteInterfaceProvider, executorService);
    }

    @POST
    @Timed
    @ApiOperation(value = "Shutdown node gracefully.",
            notes = "Attempts to process all buffered and cached messages before exiting, " +
                    "shuts down inputs first to make sure that no new messages are accepted.")
    @AuditEvent(type = AuditEventTypes.NODE_SHUTDOWN_INITIATE)
    public void shutdown(@ApiParam(name = "nodeId", value = "The id of the node to shutdown.", required = true)
                         @PathParam("nodeId") String nodeId) throws IOException, NodeNotFoundException {
        final Node targetNode = nodeService.byNodeId(nodeId);

        RemoteSystemShutdownResource remoteSystemShutdownResource = remoteInterfaceProvider.get(targetNode,
                this.authenticationToken,
                RemoteSystemShutdownResource.class);
        final Response response = remoteSystemShutdownResource.shutdown().execute();
        if (response.code() != ACCEPTED.getStatusCode()) {
            LOG.warn("Unable send shut down signal to node {}: {}", nodeId, response.message());
            throw new WebApplicationException(response.message(), BAD_GATEWAY);
        }
    }
}

