package poltrona.validation.statusValido;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StatusValidoValidator implements ConstraintValidator<StatusValido, Object> {
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return value != null;
    }
}