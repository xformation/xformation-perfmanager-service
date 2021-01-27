/*
 * */
package com.synectiks.process.server.inputs.converters;

import com.google.common.primitives.Ints;
import com.synectiks.process.server.plugin.inputs.Converter;

import java.util.Map;

/**
 * @author Lennart Koopmann <lennart@torch.sh>
 */
public class SyslogPriLevelConverter extends Converter {

    public SyslogPriLevelConverter(Map<String, Object> config) {
        super(Type.SYSLOG_PRI_LEVEL, config);
    }

    @Override
    public Object convert(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        Integer priority = Ints.tryParse(value);

        if (priority == null) {
            return value;
        }

        return SyslogPriUtilities.levelFromPriority(priority);
    }

    @Override
    public boolean buildsMultipleFields() {
        return false;
    }

}
