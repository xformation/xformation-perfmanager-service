/*
 * */
package com.synectiks.process.common.plugins.views.search.rest;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.indexer.fieldtypes.FieldTypes;

@AutoValue
@JsonAutoDetect
public abstract class MappedFieldTypeDTO {
    @JsonProperty("name")
    public abstract String name();

    @JsonProperty("type")
    public abstract FieldTypes.Type type();

    @JsonCreator
    public static MappedFieldTypeDTO create(@JsonProperty("name") String name, @JsonProperty("type") FieldTypes.Type type) {
        return new AutoValue_MappedFieldTypeDTO(name, type);
    }
}
