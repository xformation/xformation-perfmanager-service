/*
 * */
package com.synectiks.process.server.database.validators;

import com.synectiks.process.server.plugin.database.validators.ValidationResult;
import com.synectiks.process.server.plugin.database.validators.Validator;

/**
 * @author Lennart Koopmann <lennart@torch.sh>
 */
public class FilledStringValidator implements Validator {

    /**
     * Validates: Object is not null, of type String and not empty.
     *
     *
     * @param value The object to check
     * @return validation result
     */
    @Override
    public ValidationResult validate(Object value) {
        if (value != null && value instanceof String && !((String) value).isEmpty())
            return new ValidationResult.ValidationPassed();
        else
            return new ValidationResult.ValidationFailed("Value \"" + value + "\" is not a valid non-empty String!");
    }

}
