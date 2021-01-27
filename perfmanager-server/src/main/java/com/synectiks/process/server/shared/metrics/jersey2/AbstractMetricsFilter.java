/*
 * */
package com.synectiks.process.server.shared.metrics.jersey2;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import java.io.IOException;
import java.lang.reflect.Method;

import static com.codahale.metrics.MetricRegistry.name;

@Priority(Priorities.HEADER_DECORATOR)
public abstract class AbstractMetricsFilter implements ContainerRequestFilter, ContainerResponseFilter {

    protected String chooseName(String explicitName, boolean absolute, Method method, String... suffixes) {
        if (explicitName != null && !explicitName.isEmpty()) {
            if (absolute) {
                return explicitName;
            }
            return name(method.getDeclaringClass(), explicitName);
        }
        return name(name(method.getDeclaringClass(),
                method.getName()),
                suffixes);
    }

    @Override
    public abstract void filter(ContainerRequestContext requestContext) throws IOException;

    @Override
    public abstract void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException;
}
