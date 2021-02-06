/*
 * */
package com.synectiks.process.common.plugins.views.search.filter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.google.common.collect.Sets;
import com.synectiks.process.common.plugins.views.search.Filter;

import java.util.Set;

@AutoValue
@JsonTypeName(OrFilter.NAME)
@JsonDeserialize(builder = OrFilter.Builder.class)
public abstract class OrFilter implements Filter {
    public static final String NAME = "or";

    @Override
    @JsonProperty
    public abstract String type();

    @Override
    @JsonProperty
    public abstract Set<Filter> filters();

    public static Builder builder() {
        return Builder.create();
    }

    public static OrFilter or(Filter... filters) {
        return builder()
                .filters(Sets.newHashSet(filters))
                .build();
    }

    public abstract Builder toBuilder();

    @Override
    public Filter.Builder toGenericBuilder() {
        return toBuilder();
    }

    @AutoValue.Builder
    public abstract static class Builder implements Filter.Builder {
        @JsonProperty
        public abstract Builder type(String type);

        @JsonProperty
        public abstract Builder filters(Set<Filter> filters);

        @JsonProperty
        public abstract OrFilter build();

        @JsonCreator
        public static Builder create() {
            return new AutoValue_OrFilter.Builder().type(NAME);
        }
    }
}
