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
public abstract class LoggersSummary {
    @JsonProperty
    public abstract int total();
    @JsonProperty
    public abstract Map<String, SingleLoggerSummary> loggers();

    @JsonCreator
    public static LoggersSummary create(@JsonProperty("total") int total, @JsonProperty("loggers") Map<String, SingleLoggerSummary> loggers) {
        return new AutoValue_LoggersSummary(total, loggers);
    }

    public static LoggersSummary create(@JsonProperty("loggers") Map<String, SingleLoggerSummary> loggers) {
        return new AutoValue_LoggersSummary(loggers.size(), loggers);
    }
}
