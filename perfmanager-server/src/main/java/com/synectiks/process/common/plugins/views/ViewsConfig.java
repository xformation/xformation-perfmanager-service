/*
 * */
package com.synectiks.process.common.plugins.views;

import com.github.joschi.jadconfig.Parameter;
import com.synectiks.process.server.plugin.PluginConfigBean;

import org.joda.time.Duration;

public class ViewsConfig implements PluginConfigBean {
    private static final Duration DEFAULT_MAXIMUM_AGE_FOR_SEARCHES = Duration.standardDays(4);
    private static final String PREFIX = "views_";
    private static final String MAX_SEARCH_AGE = PREFIX + "maximum_search_age";

    @Parameter(MAX_SEARCH_AGE)
    private Duration maxSearchAge = DEFAULT_MAXIMUM_AGE_FOR_SEARCHES;
}
