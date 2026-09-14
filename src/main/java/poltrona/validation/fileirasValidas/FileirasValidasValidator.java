package poltrona.validation.fileirasValidas;

import java.util.Map;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FileirasValidasValidator implements ConstraintValidator<FileirasValidas, Map<Character, Integer>> {

    @Override
    public boolean isValid(Map<Character, Integer> value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        if (value.isEmpty()) {
            return false;
        }

        for (Map.Entry<Character, Integer> entry : value.entrySet()) {
            Character fileira = entry.getKey();
            Integer quantidadePoltronas = entry.getValue();

            // Valida a letra da fileira (deve ser letra maiúscula de A a Z)
            if (fileira == null || !Character.isUpperCase(fileira) || !Character.isLetter(fileira)) {
                return false;
            }

            // Valida a quantidade de assentos por fileira (ex: entre 1 e 100)
            if (quantidadePoltronas == null || quantidadePoltronas < 1 || quantidadePoltronas > 100) {
                return false;
            }
        }

        return true;
    }
}