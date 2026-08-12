package poltrona.validation.TituloValido;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TituloValidoValidator implements ConstraintValidator<TituloValido, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return false;
        String trimmed = value.trim();
        return !trimmed.isEmpty() && trimmed.length() <= 150;
    }
}