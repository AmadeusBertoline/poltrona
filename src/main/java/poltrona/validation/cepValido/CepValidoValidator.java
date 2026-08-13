package poltrona.validation.cepValido;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CepValidoValidator implements ConstraintValidator<CepValido, String> {
    private static final String REGEX_CEP = "^(\\d{8}|\\d{5}-\\d{3})$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank())
            return false;
        return value.trim().matches(REGEX_CEP);
    }
}