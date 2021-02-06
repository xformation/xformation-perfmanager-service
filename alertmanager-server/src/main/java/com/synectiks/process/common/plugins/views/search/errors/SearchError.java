/*
 * */
package com.synectiks.process.common.plugins.views.search.errors;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonSubTypes({
        @JsonSubTypes.Type(name = "query", value = QueryError.class),
        @JsonSubTypes.Type(name = "search_type", value = SearchTypeError.class),
        @JsonSubTypes.Type(name = "unbound_parameter", value = UnboundParameterError.class),
        @JsonSubTypes.Type(name = "result_window_limit", value = ResultWindowLimitError.class),
})
@JsonTypeInfo(property = "type", visible = true, use= JsonTypeInfo.Id.NAME)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY)
public interface SearchError {
    @JsonProperty("description")
    String description();
}
