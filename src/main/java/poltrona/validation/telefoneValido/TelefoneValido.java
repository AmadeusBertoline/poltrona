package poltrona.validation.telefoneValido;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TelefoneValidoValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface TelefoneValido {
    String message() default "Telefone inválido. Informe um número com DDD válido.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}