/*
 * */
package com.synectiks.process.common.events.fields;

import com.google.auto.value.AutoValue;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Longs;

import java.util.Optional;

@AutoValue
public abstract class FieldValue {
    public abstract FieldValueType dataType();

    public abstract String value();

    public static FieldValue create(FieldValueType dataType, String value) {
        return builder()
                .dataType(dataType)
                .value(value)
                .build();
    }

    public static FieldValue error() {
        return create(FieldValueType.ERROR, "");
    }

    public static FieldValue string(String value) {
        return create(FieldValueType.STRING, value);
    }

    public Optional<Long> longValue(String value) {
        if (dataType().validate(value).isPresent()) {
            // TODO: Do something in case of error?
            return Optional.empty();
        }
        return Optional.ofNullable(Longs.tryParse(value));
    }

    public Optional<Double> doubleValue(String value) {
        if (dataType().validate(value).isPresent()) {
            // TODO: Do something in case of error?
            return Optional.empty();
        }
        return Optional.ofNullable(Doubles.tryParse(value));
    }

    public Optional<Boolean> booleanValue(String value) {
        if (dataType().validate(value).isPresent()) {
            // TODO: Do something in case of error?
            return Optional.empty();
        }
        return Optional.of(Boolean.valueOf(value));
    }

    public boolean isError() {
        return dataType().isError();
    }

    public static Builder builder() {
        return Builder.create();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public static abstract class Builder {
        public static Builder create() {
            return new AutoValue_FieldValue.Builder();
        }

        public abstract Builder dataType(FieldValueType dataType);

        public abstract Builder value(String value);

        public abstract FieldValue build();
    }
}