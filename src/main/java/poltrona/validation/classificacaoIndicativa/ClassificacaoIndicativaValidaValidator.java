package poltrona.validation.classificacaoIndicativa;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import poltrona.enums.filme.ClassificacaoIndicativa;

public class ClassificacaoIndicativaValidaValidator
        implements ConstraintValidator<ClassificacaoIndicativaValida, ClassificacaoIndicativa> {

    @Override
    public boolean isValid(ClassificacaoIndicativa value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return true;
    }
}