package poltrona.validation.CaminhoImagemValido;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CaminhoImagemValidoValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CaminhoImagemValido {
    String message() default "O caminho da imagem deve terminar com .jpg, .jpeg, .png ou .webp.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}