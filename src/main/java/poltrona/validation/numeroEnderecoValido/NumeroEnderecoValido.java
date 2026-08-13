package poltrona.validation.numeroEnderecoValido;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NumeroEnderecoValidoValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface NumeroEnderecoValido {
    String message() default "Número de endereço inválido. Informe o número ou 'S/N'.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}