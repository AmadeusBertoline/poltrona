package poltrona.validation.fileirasValidas;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FileirasValidasValidator.class)
public @interface FileirasValidas {
    String message() default "Mapeamento de fileiras inválido. As fileiras devem ser letras de A-Z e ter entre 1 e 100 poltronas.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}