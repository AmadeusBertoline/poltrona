package poltrona.validation.caminhoImagemValido;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;

public class CaminhoImagemValidoValidator implements ConstraintValidator<CaminhoImagemValido, String> {
    private static final List<String> EXTENSOES_PERMITIDAS = List.of(".jpg", ".jpeg", ".png", ".webp");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank())
            return false;

        String cleanValue = value.trim().toLowerCase();
        return EXTENSOES_PERMITIDAS.stream().anyMatch(cleanValue::endsWith);
    }
}