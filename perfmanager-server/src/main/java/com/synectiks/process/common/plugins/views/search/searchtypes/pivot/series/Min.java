/*
 * */
package com.synectiks.process.common.plugins.views.search.searchtypes.pivot.series;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.SeriesSpec;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.TypedBuilder;

import java.util.Optional;

@AutoValue
@JsonTypeName(Min.NAME)
@JsonDeserialize(builder = Min.Builder.class)
public abstract class Min implements SeriesSpec {
    public static final String NAME = "min";
    @Override
    public abstract String type();

    @Override
    public abstract String id();

    @JsonProperty
    public abstract String field();

    public static Min.Builder builder() {
        return new AutoValue_Min.Builder().type(NAME);
    }

    @AutoValue.Builder
    public abstract static class Builder extends TypedBuilder<Min, Builder> {
        @JsonCreator
        public static Builder create() { return builder(); }

        @JsonProperty
        public abstract Builder id(String id);

        @JsonProperty
        public abstract Builder field(String field);

        abstract Optional<String> id();
        abstract String field();
        abstract Min autoBuild();

        public Min build() {
            if (!id().isPresent()) {
                id(NAME + "(" + field() + ")");
            }
            return autoBuild();
        }
    }
}
