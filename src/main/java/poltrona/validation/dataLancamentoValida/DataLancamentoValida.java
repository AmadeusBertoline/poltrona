package poltrona.validation.dataLancamentoValida;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DataLancamentoValidaValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface DataLancamentoValida {
    String message() default "A data de lançamento é inválida ou fora do período permitido.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}