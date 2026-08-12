package poltrona.validation.DuracaoValida;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DuracaoValidaValidator implements ConstraintValidator<DuracaoValida, String> {
    private static final String REGEX_DURACAO = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank())
            return false;
        return value.matches(REGEX_DURACAO);
    }
}
