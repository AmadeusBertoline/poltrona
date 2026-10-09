package poltrona.validation.nomeValido;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NomeValidoValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NomeValido {
    String message() default "Nome inválido. Deve conter entre 2 e 100 caracteres.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}