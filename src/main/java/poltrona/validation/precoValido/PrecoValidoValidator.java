package poltrona.validation.precoValido;

import java.math.BigDecimal;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PrecoValidoValidator implements ConstraintValidator<PrecoValido, BigDecimal> {

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        return value.compareTo(BigDecimal.ZERO) > 0;

    }

}
