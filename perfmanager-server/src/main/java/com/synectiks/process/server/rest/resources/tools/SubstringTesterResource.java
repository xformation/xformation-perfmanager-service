/*
 * */
package com.synectiks.process.server.rest.resources.tools;

import com.codahale.metrics.annotation.Timed;
import com.synectiks.process.server.audit.jersey.NoAuditEvent;
import com.synectiks.process.server.plugin.Tools;
import com.synectiks.process.server.rest.models.tools.requests.SubstringTestRequest;
import com.synectiks.process.server.rest.models.tools.responses.SubstringTesterResponse;
import com.synectiks.process.server.shared.rest.resources.RestResource;

import org.apache.shiro.authz.annotation.RequiresAuthentication;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@RequiresAuthentication
@Path("/tools/substring_tester")
public class SubstringTesterResource extends RestResource {
    @GET
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    public SubstringTesterResponse substringTester(@QueryParam("begin_index") @Min(0) int beginIndex,
                                                   @QueryParam("end_index") @Min(1) int endIndex,
                                                   @QueryParam("string") @NotNull String string) {
        return doSubstringTest(string, beginIndex, endIndex);
    }

    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @NoAuditEvent("only used for testing substring extractor")
    public SubstringTesterResponse testSubstring(@Valid @NotNull SubstringTestRequest substringTestRequest) {
        return doSubstringTest(substringTestRequest.string(), substringTestRequest.start(), substringTestRequest.end());
    }

    private SubstringTesterResponse doSubstringTest(String string, int beginIndex, int endIndex) {
        final String cut = Tools.safeSubstring(string, beginIndex, endIndex);

        return SubstringTesterResponse.create(cut != null, cut, beginIndex, endIndex);
    }
}