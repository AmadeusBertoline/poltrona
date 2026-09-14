package poltrona.validation.tipoPoltronaValida;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import poltrona.enums.poltrona.TipoPoltrona;

public class TipoPoltronaValidaValidator 
        implements ConstraintValidator<TipoPoltronaValida, TipoPoltrona> {

    @Override
    public boolean isValid(TipoPoltrona value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return true;
    }
}