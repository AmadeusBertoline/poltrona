package poltrona.validation.formatoFilme;

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
@Constraint(validatedBy = FormatoFilmeValidoValidator.class)
public @interface FormatoFilmeValido {
    String message() default "Formato de filme inválido.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}