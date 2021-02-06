/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.strings;

import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

public class Lowercase extends StringUtilsFunction {

    public static final String NAME = "lowercase";

    @Override
    protected String getName() {
        return NAME;
    }

    @Override
    protected String description() {
        return "Lowercases a string";
    }

    @Override
    protected boolean isLocaleAware() {
        return true;
    }

    @Override
    protected String apply(String value, Locale locale) {
        return StringUtils.lowerCase(value, locale);
    }
}
