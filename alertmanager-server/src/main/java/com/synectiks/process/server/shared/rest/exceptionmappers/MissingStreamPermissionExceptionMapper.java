/*
 * */
package com.synectiks.process.server.shared.rest.exceptionmappers;

import com.synectiks.process.server.plugin.rest.MissingStreamPermissionError;
import com.synectiks.process.server.shared.rest.exceptions.MissingStreamPermissionException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class MissingStreamPermissionExceptionMapper implements ExceptionMapper<MissingStreamPermissionException> {
    @Override
    public Response toResponse(MissingStreamPermissionException e) {
        final MissingStreamPermissionError missingStreamPermissionError = MissingStreamPermissionError.builder()
                .errorMessage(e.getMessage())
                .streams(e.streamsWithMissingPermissions())
                .build();
        return Response.status(Response.Status.FORBIDDEN)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(missingStreamPermissionError)
                .build();
    }
}

