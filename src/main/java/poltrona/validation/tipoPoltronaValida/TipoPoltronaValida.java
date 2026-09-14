package poltrona.validation.tipoPoltronaValida;

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
@Constraint(validatedBy = TipoPoltronaValidaValidator.class)
public @interface TipoPoltronaValida {
    String message() default "Tipo de poltrona inválido.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}