/*
 * */
package com.synectiks.process.server.lookup.events;

import com.google.auto.value.AutoValue;
import com.synectiks.process.server.lookup.dto.LookupTableDto;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@AutoValue
public abstract class LookupTablesDeleted {
    @JsonProperty("lookup_table_ids")
    public abstract Set<String> lookupTableIds();

    @JsonProperty("lookup_table_names")
    public abstract Set<String> lookupTableNames();

    @JsonCreator
    public static LookupTablesDeleted create(@JsonProperty("lookup_table_ids") Set<String> lookupTableIds,
                                             @JsonProperty("lookup_table_names") Set<String> lookupTableNames) {
        return new AutoValue_LookupTablesDeleted(lookupTableIds, lookupTableNames);
    }

    public static LookupTablesDeleted create(LookupTableDto dto) {
        return create(Collections.singleton(dto.id()), Collections.singleton(dto.name()));
    }

    public static LookupTablesDeleted create(Collection<LookupTableDto> dtos) {
        return create(dtos.stream().map(LookupTableDto::id).collect(Collectors.toSet()),
                dtos.stream().map(LookupTableDto::name).collect(Collectors.toSet()));
    }
}
