package poltrona.validation.precoValido;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PrecoValidoValidator.class)
@NotNull(message = "O preço do ingresso é obrigatório")
public @interface PrecoValido {

    String message() default "O preço do ingresso deve ser maior que zero.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
