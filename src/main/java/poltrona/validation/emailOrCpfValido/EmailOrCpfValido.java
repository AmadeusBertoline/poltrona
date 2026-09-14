package poltrona.validation.emailOrCpfValido;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailOrCpfValidoValidator.class)
public @interface EmailOrCpfValido {
    String message() default "Identificador inválido. Informe um e-mail ou CPF válido.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}