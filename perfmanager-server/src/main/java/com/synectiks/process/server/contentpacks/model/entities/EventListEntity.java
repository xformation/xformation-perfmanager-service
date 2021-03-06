/*
 * */
package com.synectiks.process.server.contentpacks.model.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.synectiks.process.common.plugins.views.search.Filter;
import com.synectiks.process.common.plugins.views.search.SearchType;
import com.synectiks.process.common.plugins.views.search.engine.BackendQuery;
import com.synectiks.process.common.plugins.views.search.searchtypes.events.EventList;
import com.synectiks.process.common.plugins.views.search.timeranges.DerivedTimeRange;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueReference;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@AutoValue
@JsonTypeName(EventListEntity.NAME)
@JsonDeserialize(builder = EventListEntity.Builder.class)
public abstract class EventListEntity implements SearchTypeEntity {
    public static final String NAME = "events";

    @Override
    public abstract String type();

    @Override
    @Nullable
    @JsonProperty
    public abstract String id();

    @Nullable
    @Override
    public abstract Filter filter();

    @JsonCreator
    public static Builder builder() {
        return new AutoValue_EventListEntity.Builder()
                .type(NAME)
                .streams(Collections.emptySet());
    }

    public abstract EventListEntity.Builder toBuilder();

    @Override
    public SearchTypeEntity.Builder toGenericBuilder() {
        return toBuilder();
    }

    @AutoValue.Builder
    public abstract static class Builder implements SearchTypeEntity.Builder{
        @JsonCreator
        public static Builder createDefault() {
            return builder()
                    .streams(Collections.emptySet());
        }

        @JsonProperty
        public abstract Builder type(String type);

        @JsonProperty
        public abstract Builder id(String id);

        @JsonProperty
        public abstract Builder name(@Nullable String name);

        @JsonProperty
        public abstract Builder filter(@Nullable Filter filter);

        @JsonProperty
        public abstract Builder query(@Nullable BackendQuery query);

        @JsonProperty
        public abstract Builder timerange(@Nullable DerivedTimeRange timeRange);

        @JsonProperty
        public abstract Builder streams(Set<String> streams);

        public abstract EventListEntity build();
    }

    @Override
    public SearchType toNativeEntity(Map<String, ValueReference> parameters, Map<EntityDescriptor, Object> nativeEntities) {
        return EventList.builder()
                .type(type())
                .streams(mappedStreams(nativeEntities))
                .id(id())
                .filter(filter())
                .query(query().orElse(null))
                .timerange(timerange().orElse(null))
                .name(name().orElse(null))
                .build();
    }
}
