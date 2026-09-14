package poltrona.validation.formatoFilme;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import poltrona.enums.filme.FormatoFilme;

public class FormatoFilmeValidoValidator
        implements ConstraintValidator<FormatoFilmeValido, FormatoFilme> {

    @Override
    public boolean isValid(FormatoFilme value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return true;
    }
}