/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.encoding;

import com.google.common.io.BaseEncoding;

import java.nio.charset.StandardCharsets;

public class Base32HumanDecode extends BaseEncodingSingleArgStringFunction {
    public static final String NAME = "base32human_decode";
    private static final String ENCODING_NAME = "base32 (human-friendly)";

    @Override
    protected String getEncodedValue(String value, boolean omitPadding) {
        BaseEncoding encoding = BaseEncoding.base32();
        encoding = omitPadding ? encoding.omitPadding() : encoding;

        return new String(encoding.decode(value), StandardCharsets.UTF_8);
    }

    @Override
    protected String getEncodingName() {
        return ENCODING_NAME;
    }

    @Override
    protected String getName() {
        return NAME;
    }
}