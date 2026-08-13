package poltrona.validation.quantidadeSalasValida;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = QuantidadeSalasValidaValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface QuantidadeSalasValida {
    String message() default "A quantidade de salas deve ser entre 1 e 100.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}