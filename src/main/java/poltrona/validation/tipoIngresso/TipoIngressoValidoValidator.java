package poltrona.validation.tipoIngresso;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import poltrona.enums.ingresso.TipoIngresso;

public class TipoIngressoValidoValidator 
        implements ConstraintValidator<TipoIngressoValido, TipoIngresso> {

    @Override
    public boolean isValid(TipoIngresso value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        return true;
    }
}