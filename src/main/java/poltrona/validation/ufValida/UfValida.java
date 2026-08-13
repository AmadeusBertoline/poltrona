package poltrona.validation.ufValida;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UfValidaValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface UfValida {
    String message() default "UF inválida. Informe a sigla de um estado brasileiro (ex: SP, RJ).";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}