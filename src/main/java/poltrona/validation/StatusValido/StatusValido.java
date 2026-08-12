package poltrona.validation.StatusValido;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StatusValidoValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface StatusValido {
    String message() default "O status do filme é obrigatório e deve ser válido.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}