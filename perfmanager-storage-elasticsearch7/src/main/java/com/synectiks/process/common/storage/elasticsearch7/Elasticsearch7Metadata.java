/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch7;

import com.synectiks.process.server.plugin.PluginMetaData;
import com.synectiks.process.server.plugin.ServerStatus;
import com.synectiks.process.server.plugin.Version;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

public class Elasticsearch7Metadata implements PluginMetaData {
    @Override
    public String getUniqueId() {
        return Elasticsearch7Plugin.class.getCanonicalName();
    }

    @Override
    public String getName() {
        return "Elasticsearch 7 Support";
    }

    @Override
    public String getAuthor() {
        return "Graylog, Inc.";
    }

    @Override
    public URI getURL() {
        return URI.create("https://www.graylog.org");
    }

    @Override
    public Version getVersion() {
        return Version.CURRENT_CLASSPATH;
    }

    @Override
    public String getDescription() {
        return "Support for Elasticsearch 7";
    }

    @Override
    public Version getRequiredVersion() {
        return Version.CURRENT_CLASSPATH;
    }

    @Override
    public Set<ServerStatus.Capability> getRequiredCapabilities() {
        return Collections.emptySet();
    }
}
