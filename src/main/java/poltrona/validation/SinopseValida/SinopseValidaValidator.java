package poltrona.validation.SinopseValida;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SinopseValidaValidator implements ConstraintValidator<SinopseValida, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null)
            return false;
        String trimmed = value.trim();
        return trimmed.length() >= 10 && trimmed.length() <= 2000;
    }
}