package poltrona.validation.emailOrCpfValido;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmailOrCpfValidoValidator implements ConstraintValidator<EmailOrCpfValido, String> {

    private static final String REGEX_EMAIL = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,}$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        if (value.isBlank()) {
            return false;
        }

        String trimmed = value.trim();

        return isEmailValido(trimmed) || isCpfValido(trimmed);
    }

    private boolean isEmailValido(String email) {
        return email.matches(REGEX_EMAIL);
    }

    private boolean isCpfValido(String cpf) {
        String apenasNumeros = cpf.replaceAll("\\D", "");

        if (apenasNumeros.length() != 11 || apenasNumeros.matches("(\\d)\\1{10}")) {
            return false;
        }

        try {
            int soma1 = 0;
            for (int i = 0; i < 9; i++) {
                soma1 += (apenasNumeros.charAt(i) - '0') * (10 - i);
            }
            int digito1 = 11 - (soma1 % 11);
            if (digito1 >= 10)
                digito1 = 0;

            if (digito1 != (apenasNumeros.charAt(9) - '0')) {
                return false;
            }

            int soma2 = 0;
            for (int i = 0; i < 10; i++) {
                soma2 += (apenasNumeros.charAt(i) - '0') * (11 - i);
            }
            int digito2 = 11 - (soma2 % 11);
            if (digito2 >= 10)
                digito2 = 0;

            return digito2 == (apenasNumeros.charAt(10) - '0');
        } catch (Exception e) {
            return false;
        }
    }
}