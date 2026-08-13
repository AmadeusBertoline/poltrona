package poltrona.validation.cnpjValido;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CnpjValidoValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CnpjValido {
    String message() default "CNPJ inválido. Informe 14 dígitos válidos.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}