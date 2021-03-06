/*
 * */
package com.synectiks.process.server.plugin.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import java.util.Set;

@AutoValue
@JsonDeserialize(builder = MissingStreamPermissionError.Builder.class)
public abstract class MissingStreamPermissionError {

    private static final String FIELD_ERROR_MESSAGE = "message";
    private static final String FIELD_STREAMS = "streams";
    private static final String FIELD_TYPE = "type";

    @JsonProperty(FIELD_ERROR_MESSAGE)
    public abstract String errorMessage();

    @JsonProperty(FIELD_STREAMS)
    public abstract Set<String> streams();

    @JsonProperty(FIELD_TYPE)
    public abstract String type();

    public static Builder builder() {
        return Builder.create();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public static abstract class Builder {

        @JsonCreator
        public static Builder create() {
            return new AutoValue_MissingStreamPermissionError.Builder()
                    .type("MissingStreamPermission");
        }

        @JsonProperty(FIELD_ERROR_MESSAGE)
        public abstract Builder errorMessage(String errorMessage);

        @JsonProperty(FIELD_STREAMS)
        public abstract Builder streams(Set<String> streams);

        @JsonProperty(FIELD_TYPE)
        public abstract Builder type(String type);

        public abstract MissingStreamPermissionError build();
    }
}
