package poltrona.validation.duracaoValida;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DuracaoValidaValidator implements ConstraintValidator<DuracaoValida, Integer> {
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null)
            return true;

        return value > 0 && value <= 600;
    }
}