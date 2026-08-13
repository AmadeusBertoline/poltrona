package poltrona.validation.nomeValido;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NomeValidoValidator implements ConstraintValidator<NomeValido, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return false;
        String trimmed = value.trim();
        return trimmed.length() >= 2 && trimmed.length() <= 100;
    }
}