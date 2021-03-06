/*
 * */
package com.synectiks.process.common.events.fields;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.synectiks.process.common.events.fields.providers.FieldValueProvider;

import java.util.List;

@AutoValue
@JsonDeserialize(builder = EventFieldSpec.Builder.class)
public abstract class EventFieldSpec {
    private static final String FIELD_DATA_TYPE = "data_type";
    private static final String FIELD_PROVIDERS = "providers";

    @JsonProperty(FIELD_DATA_TYPE)
    public abstract FieldValueType dataType();

    @JsonProperty(FIELD_PROVIDERS)
    public abstract List<FieldValueProvider.Config> providers();

    public static Builder builder() {
        return Builder.create();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public static abstract class Builder {
        @JsonCreator
        public static Builder create() {
            return new AutoValue_EventFieldSpec.Builder();
        }

        @JsonProperty(FIELD_DATA_TYPE)
        public abstract Builder dataType(FieldValueType dataType);

        @JsonProperty(FIELD_PROVIDERS)
        public abstract Builder providers(List<FieldValueProvider.Config> providers);

        public abstract EventFieldSpec build();
    }
}