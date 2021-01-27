/*
 * */
package com.synectiks.process.server.rest.models.tools.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import javax.validation.constraints.NotEmpty;
import java.util.Map;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class JsonTesterResponse {
    @JsonProperty("matches")
    public abstract Map<String, Object> matches();

    @JsonProperty("flatten")
    public abstract boolean flatten();

    @JsonProperty("line_separator")
    @NotEmpty
    public abstract String listSeparator();

    @JsonProperty("key_separator")
    @NotEmpty
    public abstract String keySeparator();

    @JsonProperty("kv_separator")
    @NotEmpty
    public abstract String kvSeparator();

    @JsonProperty("string")
    @NotEmpty
    public abstract String string();

    @JsonCreator
    public static JsonTesterResponse create(@JsonProperty("matches") Map<String, Object> matches,
                                            @JsonProperty("flatten") boolean flatten,
                                            @JsonProperty("line_separator") @NotEmpty String listSeparator,
                                            @JsonProperty("key_separator") @NotEmpty String keySeparator,
                                            @JsonProperty("kv_separator") @NotEmpty String kvSeparator,
                                            @JsonProperty("string") @NotEmpty String string) {
        return new AutoValue_JsonTesterResponse(matches, flatten, listSeparator, keySeparator, kvSeparator, string);
    }
}