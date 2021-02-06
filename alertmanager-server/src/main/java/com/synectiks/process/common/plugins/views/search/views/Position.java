/*
 * */
package com.synectiks.process.common.plugins.views.search.views;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class Position {
    private static Position infinity() {
        return new Infinity();
    }

    public static Position fromInt(int value) {
        return new NumberPosition(value);
    }

    @JsonCreator
    public static Position fromJson(Object value) {
        if (value instanceof Integer) {
            return fromInt((int)value);
        }
        if (value instanceof Double && value.equals(Infinity.value)) {
            return infinity();
        }
        if (value instanceof String && value.equals("Infinity")) {
            return infinity();
        }
        throw new IllegalArgumentException("Unable to deserialize " + value + " to Position.");
    }
}

class Infinity extends Position {
    static final Double value = Double.POSITIVE_INFINITY;

    Infinity() {
        super();
    }

    @JsonValue
    public Double jsonValue() {
        return value;
    }
}

class NumberPosition extends Position {
    private final int value;

    NumberPosition(int value) {
        super();
        this.value = value;
    }

    @JsonValue
    public int jsonValue() {
        return this.value;
    }
}
