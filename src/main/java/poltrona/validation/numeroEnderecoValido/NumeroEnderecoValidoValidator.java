package poltrona.validation.numeroEnderecoValido;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NumeroEnderecoValidoValidator implements ConstraintValidator<NumeroEnderecoValido, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank())
            return false;
        int length = value.trim().length();
        return length >= 1 && length <= 20;
    }
}