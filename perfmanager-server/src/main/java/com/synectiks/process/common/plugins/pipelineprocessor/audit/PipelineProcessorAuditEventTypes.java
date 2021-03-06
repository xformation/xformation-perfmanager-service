/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.audit;

import com.google.common.collect.ImmutableSet;
import com.synectiks.process.server.audit.PluginAuditEventTypes;

import java.util.Set;

public class PipelineProcessorAuditEventTypes implements PluginAuditEventTypes {
    private static final String NAMESPACE = "pipeline_processor:";

    public static final String PIPELINE_CONNECTION_UPDATE = NAMESPACE + "pipeline_connection:update";
    public static final String PIPELINE_CREATE = NAMESPACE + "pipeline:create";
    public static final String PIPELINE_UPDATE = NAMESPACE + "pipeline:update";
    public static final String PIPELINE_DELETE = NAMESPACE + "pipeline:delete";
    public static final String RULE_CREATE = NAMESPACE + "rule:create";
    public static final String RULE_UPDATE = NAMESPACE + "rule:update";
    public static final String RULE_DELETE = NAMESPACE + "rule:delete";
    public static final String RULE_METRICS_UPDATE = NAMESPACE + "rulemetrics:update";

    private static final ImmutableSet<String> EVENT_TYPES = ImmutableSet.<String>builder()
            .add(PIPELINE_CONNECTION_UPDATE)
            .add(PIPELINE_CREATE)
            .add(PIPELINE_UPDATE)
            .add(PIPELINE_DELETE)
            .add(RULE_CREATE)
            .add(RULE_UPDATE)
            .add(RULE_DELETE)
            .add(RULE_METRICS_UPDATE)
            .build();

    @Override
    public Set<String> auditEventTypes() {
        return EVENT_TYPES;
    }
}
