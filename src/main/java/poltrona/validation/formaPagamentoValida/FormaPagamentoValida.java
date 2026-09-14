package poltrona.validation.formaPagamentoValida;

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
@Constraint(validatedBy = FormaPagamentoValidaValidator.class)
public @interface FormaPagamentoValida {
    String message() default "Forma de pagamento inválida.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}