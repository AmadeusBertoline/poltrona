package poltrona.validation.quantidadeSalasValida;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class QuantidadeSalasValidaValidator implements ConstraintValidator<QuantidadeSalasValida, Integer> {
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) return false;
        return value >= 1 && value <= 100;
    }
}