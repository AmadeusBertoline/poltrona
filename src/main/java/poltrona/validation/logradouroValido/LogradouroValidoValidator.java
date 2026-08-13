package poltrona.validation.logradouroValido;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LogradouroValidoValidator implements ConstraintValidator<LogradouroValido, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank())
            return false;
        int length = value.trim().length();
        return length >= 3 && length <= 150;
    }
}