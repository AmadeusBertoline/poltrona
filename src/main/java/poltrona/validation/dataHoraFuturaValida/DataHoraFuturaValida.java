package poltrona.validation.dataHoraFuturaValida;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@NotNull(message = "A data e hora é obrigatória.")
@Future(message = "A data e hora deve ser no futuro.")
public @interface DataHoraFuturaValida {
    String message() default "Data inválida";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}