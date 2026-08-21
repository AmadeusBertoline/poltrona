package poltrona.validation.generosValidos;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import poltrona.enums.GeneroFilme;

import java.util.Set;

public class GenerosValidosValidator
        implements ConstraintValidator<GenerosValidos, Set<GeneroFilme>> {

    @Override
    public boolean isValid(
            Set<GeneroFilme> value,
            ConstraintValidatorContext context) {

        if (value == null || value.isEmpty()) {
            return false;
        }

        return value.stream().noneMatch(genero -> genero == null);
    }
}