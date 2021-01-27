/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@AutoValue
@WithBeanGetter
abstract class ViewState {
    private static final String FIELD_SELECTED_FIELDS = "selected_fields";
    private static final String FIELD_STATIC_MESSAGE_LIST_ID = "static_message_list_id";
    private static final String FIELD_TITLES = "titles";
    private static final String FIELD_WIDGETS = "widgets";
    private static final String FIELD_WIDGET_MAPPING = "widget_mapping";
    private static final String FIELD_WIDGET_POSITIONS = "positions";
    private static final String FIELD_DISPLAY_MODE_SETTINGS = "display_mode_settings";

    @JsonProperty(FIELD_SELECTED_FIELDS)
    Optional<Set<String>> fields() {
        return Optional.empty();
    }

    @JsonProperty(FIELD_STATIC_MESSAGE_LIST_ID)
    Optional<String> staticMessageListId() {
        return Optional.empty();
    }

    @JsonProperty(FIELD_TITLES)
    abstract Titles titles();

    @JsonProperty(FIELD_WIDGETS)
    abstract Set<ViewWidget> widgets();

    @JsonProperty(FIELD_WIDGET_MAPPING)
    abstract Map<String, Set<String>> widgetMapping();

    @JsonProperty(FIELD_WIDGET_POSITIONS)
    abstract Map<String, ViewWidgetPosition> widgetPositions();

    @JsonProperty(FIELD_DISPLAY_MODE_SETTINGS)
    DisplayModeSettings displayModeSettings() {
        return DisplayModeSettings.empty();
    }

    static ViewState create(Titles titles,
                  Set<ViewWidget> widgets,
                  Map<String, Set<String>> widgetMapping,
                  Map<String, ViewWidgetPosition> widgetPositions) {
        return new AutoValue_ViewState(titles, widgets, widgetMapping, widgetPositions);
    }
}
