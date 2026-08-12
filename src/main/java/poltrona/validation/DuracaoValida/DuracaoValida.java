package poltrona.validation.DuracaoValida;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DuracaoValidaValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface DuracaoValida {
    String message() default "A duração deve estar no formato HH:mm:ss (ex: 02:15:00).";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}