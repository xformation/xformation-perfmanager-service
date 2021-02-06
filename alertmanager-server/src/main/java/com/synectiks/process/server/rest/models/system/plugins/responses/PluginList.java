/*
 * */
package com.synectiks.process.server.rest.models.system.plugins.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.util.List;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class PluginList {
    @JsonProperty
    public abstract List<PluginMetaDataValue> plugins();

    @JsonProperty
    public abstract int total();

    @JsonCreator
    public static PluginList create(@JsonProperty("plugins") List<PluginMetaDataValue> plugins,
                                    @JsonProperty("total") int total) {
        return new AutoValue_PluginList(plugins, total);
    }

    public static PluginList create(@JsonProperty("plugins") List<PluginMetaDataValue> plugins) {
        return create(plugins, plugins.size());
    }

}
