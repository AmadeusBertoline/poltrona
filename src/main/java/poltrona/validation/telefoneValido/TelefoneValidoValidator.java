package poltrona.validation.telefoneValido;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TelefoneValidoValidator implements ConstraintValidator<TelefoneValido, String> {

    private static final String REGEX_TELEFONE = "^\\(?[1-9]{2}\\)?\\s?9?\\d{4}-?\\d{4}$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return false;
        return value.trim().matches(REGEX_TELEFONE);
    }
}