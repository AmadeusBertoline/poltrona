package poltrona.validation.dataHoraFuturaValida;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Future;

@Documented
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@Future(message = "A data e hora deve ser no futuro.")
public @interface DataHoraFuturaValida {
    String message() default "A data e hora deve ser no futuro.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}