/*
 * */
package com.synectiks.process.server.rest.resources.tools;

import com.codahale.metrics.annotation.Timed;
import com.synectiks.process.server.audit.jersey.NoAuditEvent;
import com.synectiks.process.server.rest.models.tools.requests.RegexTestRequest;
import com.synectiks.process.server.rest.models.tools.responses.RegexTesterResponse;
import com.synectiks.process.server.rest.models.tools.responses.RegexValidationResponse;
import com.synectiks.process.server.shared.rest.resources.RestResource;

import org.apache.shiro.authz.annotation.RequiresAuthentication;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@RequiresAuthentication
@Path("/tools/regex_tester")
public class RegexTesterResource extends RestResource {
    @GET
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    public RegexTesterResponse regexTester(@QueryParam("regex") @NotEmpty String regex,
                                           @QueryParam("string") @NotNull String string) {
        return doTestRegex(string, regex);
    }

    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @NoAuditEvent("only used to test regex values")
    public RegexTesterResponse testRegex(@Valid @NotNull RegexTestRequest regexTestRequest) {
        return doTestRegex(regexTestRequest.string(), regexTestRequest.regex());
    }

    @GET
    @Path("/validate")
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    public RegexValidationResponse validateRegex(@QueryParam("regex") @NotEmpty String regex) {
        final RegexValidationResponse.Builder response = RegexValidationResponse.builder()
                .regex(regex);

        try {
            Pattern.compile(regex, Pattern.DOTALL);
            response.isValid(true);
        } catch (PatternSyntaxException e) {
            response.isValid(false).validationMessage(e.getMessage());
        }

        return response.build();
    }

    private RegexTesterResponse doTestRegex(String example, String regex) {
        final Pattern pattern;
        try {
            pattern = Pattern.compile(regex, Pattern.DOTALL);
        } catch (PatternSyntaxException e) {
            throw new BadRequestException("Invalid regular expression: " + e.getMessage(), e);
        }

        final Matcher matcher = pattern.matcher(example);
        boolean matched = matcher.find();

        // Get the first matched group.
        final RegexTesterResponse.Match match;
        if (matched && matcher.groupCount() > 0) {
            match = RegexTesterResponse.Match.create(matcher.group(1), matcher.start(1), matcher.end(1));
        } else {
            match = null;
        }

        return RegexTesterResponse.create(matched, match, regex, example);
    }
}
