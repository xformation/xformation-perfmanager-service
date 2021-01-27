/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.dashboardwidgets;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.base.Strings;

import javax.annotation.Nullable;

@AutoValue
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class StackedSeries {
    public abstract String query();
    public abstract String field();
    public abstract String statisticalFunction();

    @JsonCreator
    public static StackedSeries create(
            @JsonProperty("query") @Nullable String query,
            @JsonProperty("field") String field,
            @JsonProperty("statistical_function") String statisticalFunction
    ) {
        final String cleanedQuery = Strings.nullToEmpty(query).trim();
        return new AutoValue_StackedSeries(cleanedQuery.equals("") ? "*" : cleanedQuery, field, statisticalFunction);
    }
}
