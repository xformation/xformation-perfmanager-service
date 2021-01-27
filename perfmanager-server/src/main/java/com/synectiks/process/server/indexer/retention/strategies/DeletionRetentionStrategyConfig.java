/*
 * */
package com.synectiks.process.server.indexer.retention.strategies;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.plugin.indexer.retention.RetentionStrategyConfig;

import org.graylog.autovalue.WithBeanGetter;

import javax.validation.constraints.Min;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class DeletionRetentionStrategyConfig implements RetentionStrategyConfig {
    private static final int DEFAULT_MAX_NUMBER_OF_INDICES = 20;

    @JsonProperty("max_number_of_indices")
    public abstract int maxNumberOfIndices();

    @JsonCreator
    public static DeletionRetentionStrategyConfig create(@JsonProperty(TYPE_FIELD) String type,
                                                         @JsonProperty("max_number_of_indices") @Min(1) int maxNumberOfIndices) {
        return new AutoValue_DeletionRetentionStrategyConfig(type, maxNumberOfIndices);
    }

    @JsonCreator
    public static DeletionRetentionStrategyConfig create(@JsonProperty("max_number_of_indices") @Min(1) int maxNumberOfIndices) {
        return new AutoValue_DeletionRetentionStrategyConfig(DeletionRetentionStrategyConfig.class.getCanonicalName(), maxNumberOfIndices);
    }

    public static DeletionRetentionStrategyConfig createDefault() {
        return create(DEFAULT_MAX_NUMBER_OF_INDICES);
    }
}
