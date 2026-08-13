package poltrona.validation.sinopseValida;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SinopseValidaValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface SinopseValida {
    String message() default "A descrição deve conter entre 10 e 2000 caracteres.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}