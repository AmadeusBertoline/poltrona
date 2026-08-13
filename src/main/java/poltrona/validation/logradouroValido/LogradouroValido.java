package poltrona.validation.logradouroValido;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = LogradouroValidoValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface LogradouroValido {
    String message() default "Logradouro inválido. Deve conter entre 3 e 150 caracteres.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}