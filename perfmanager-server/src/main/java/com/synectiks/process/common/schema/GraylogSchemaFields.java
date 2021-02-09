/*
 * */
package com.synectiks.process.common.schema;

/**
 * Field names used in the standard perfmanager Schema.
 *
 * @deprecated Please use the appropriate enums in this package rather than this collection of strings
 */
@Deprecated
public class GraylogSchemaFields {

    public static final String FIELD_TIMESTAMP = "timestamp";

    public static final String FIELD_ILLUMINATE_EVENT_CATEGORY = "xfperf_event_category";
    public static final String FIELD_ILLUMINATE_EVENT_SUBCATEGORY = "xfperf_event_subcategory";
    public static final String FIELD_ILLUMINATE_EVENT_TYPE = "xfperf_event_type";
    public static final String FIELD_ILLUMINATE_EVENT_TYPE_CODE = "xfperf_event_type_code";
    public static final String FIELD_ILLUMINATE_TAGS = "xfperf_tags";
}
