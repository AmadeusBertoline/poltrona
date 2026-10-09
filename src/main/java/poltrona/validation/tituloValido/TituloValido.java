package poltrona.validation.tituloValido;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;      
import java.lang.annotation.RetentionPolicy; 
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = TituloValidoValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface TituloValido {
    String message() default "O título é obrigatório e deve conter entre 1 e 150 caracteres.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}