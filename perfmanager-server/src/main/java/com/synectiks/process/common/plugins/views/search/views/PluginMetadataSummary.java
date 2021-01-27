/*
 * */
package com.synectiks.process.common.plugins.views.search.views;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.zafarkhaja.semver.Version;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.plugin.PluginMetaData;

import java.net.URI;

@AutoValue
@JsonAutoDetect
public abstract class PluginMetadataSummary {
    @JsonProperty("unique_id")
    public abstract String uniqueId();

    @JsonProperty
    public abstract String name();

    @JsonProperty
    public abstract String author();

    @JsonProperty
    public abstract URI url();

    @JsonProperty
    @JsonSerialize(using = VersionSerializer.class)
    public abstract Version version();

    @JsonProperty
    public abstract String description();

    static PluginMetadataSummary create(PluginMetaData pluginMetaData) {
        return PluginMetadataSummary.create(
                pluginMetaData.getUniqueId(),
                pluginMetaData.getName(),
                pluginMetaData.getAuthor(),
                pluginMetaData.getURL(),
                pluginMetaData.getVersion().toString(),
                pluginMetaData.getDescription()
        );
    }

    @JsonCreator
    public static PluginMetadataSummary create(
            @JsonProperty("unique_id") String uniqueId,
            @JsonProperty("name") String name,
            @JsonProperty("author") String author,
            @JsonProperty("url") URI url,
            @JsonProperty("version") String version,
            @JsonProperty("description") String description
    ) {
        return new AutoValue_PluginMetadataSummary(uniqueId, name, author, url, Version.valueOf(version), description);
    }
}
