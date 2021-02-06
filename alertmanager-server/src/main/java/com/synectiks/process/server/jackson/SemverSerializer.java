/*
 * */
package com.synectiks.process.server.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.synectiks.process.server.semver4j.Semver;

import java.io.IOException;

public class SemverSerializer extends StdSerializer<Semver> {
    public SemverSerializer() {
        super(Semver.class);
    }

    @Override
    public void serialize(final Semver value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
        final String version = value.toString();
        gen.writeString(version);
    }
}
