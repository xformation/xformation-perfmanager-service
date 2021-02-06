/*
 * */
package com.synectiks.process.server.rest.models.system.inputs.extractors.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.util.List;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class ExtractorSummaryList {
    @JsonProperty
    public abstract int total();

    @JsonProperty
    public abstract List<ExtractorSummary> extractors();

    @JsonCreator
    public static ExtractorSummaryList create(@JsonProperty("total") int total,
                                              @JsonProperty("extractors") List<ExtractorSummary> extractors) {
        return new AutoValue_ExtractorSummaryList(total, extractors);
    }

    public static ExtractorSummaryList create(List<ExtractorSummary> extractors) {
        return new AutoValue_ExtractorSummaryList(extractors.size(), extractors);
    }
}
