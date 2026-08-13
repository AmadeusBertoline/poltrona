package poltrona.validation.cnpjValido;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CnpjValidoValidator implements ConstraintValidator<CnpjValido, String> {
    // Aceita 14 dígitos puros ou o formato 00.000.000/0001-00
    private static final String REGEX_CNPJ = "^(\\d{14}|\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2})$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank())
            return false;
        return value.trim().matches(REGEX_CNPJ);
    }
}