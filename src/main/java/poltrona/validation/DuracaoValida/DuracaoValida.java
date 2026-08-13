package poltrona.validation.duracaoValida;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DuracaoValidaValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface DuracaoValida {
    String message() default "A duração deve ser informada em quantidade de minutos";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}