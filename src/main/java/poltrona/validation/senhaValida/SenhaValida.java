package poltrona.validation.senhaValida;

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
@Constraint(validatedBy = SenhaValidaValidator.class)
public @interface SenhaValida {
    String message() default "A senha deve ter no mínimo 8 caracteres, contendo pelo menos uma letra maiúscula, uma letra minúscula, um número e um caractere especial.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}