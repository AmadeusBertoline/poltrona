package poltrona.validation.precoValido;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PrecoValidoValidator.class)
public @interface PrecoValido {

    String message() default "O preço do ingresso deve ser maior que zero.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
