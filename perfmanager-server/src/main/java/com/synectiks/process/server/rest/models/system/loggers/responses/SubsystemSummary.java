/*
 * */
package com.synectiks.process.server.rest.models.system.loggers.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.util.Map;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class SubsystemSummary {
    @JsonProperty
    public abstract Map<String, SingleSubsystemSummary> subsystems();

    @JsonCreator
    public static SubsystemSummary create(@JsonProperty("subsystems") Map<String, SingleSubsystemSummary> subsystems) {
        return new AutoValue_SubsystemSummary(subsystems);
    }
}
