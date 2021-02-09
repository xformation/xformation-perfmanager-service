/*
 * */
package com.synectiks.process.server.web;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.util.List;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class ChunkDescription {
    @JsonProperty("size")
    public abstract long size();

    @JsonProperty("entry")
    public abstract String entry();

    @JsonProperty("css")
    public abstract List<String> css();

    @JsonCreator
    public static ChunkDescription create(@JsonProperty("size") long size,
                                          @JsonProperty("entry") String entry,
                                          @JsonProperty("css") List<String> css) {
        return new AutoValue_ChunkDescription(size, entry, css);
    }
}
