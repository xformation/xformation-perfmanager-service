/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.viewwidgets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.RandomUUIDProvider;
import com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.SearchType;
import com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.ViewWidget;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

@AutoValue
public abstract class NonImplementedWidget implements ViewWidget {
    public static final String FIELD_ID = "id";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_CREATOR_USER_ID = "creator_user_id";
    public static final String FIELD_CONFIG = "config";

    @JsonProperty(FIELD_ID)
    public abstract String id();
    @JsonProperty(FIELD_TYPE)
    public abstract String type();
    @JsonProperty(FIELD_CONFIG)
    public abstract Map<String, Object> config();

    @Override
    public Set<SearchType> toSearchTypes(RandomUUIDProvider randomUUIDProvider) {
        return Collections.emptySet();
    }

    @JsonCreator
    public static NonImplementedWidget create(
            @JsonProperty(FIELD_ID) String id,
            @JsonProperty(FIELD_TYPE) String type,
            @JsonProperty(FIELD_CONFIG) Map<String, Object> config
    ) {
        return new AutoValue_NonImplementedWidget(id, type, config);
    }
}
