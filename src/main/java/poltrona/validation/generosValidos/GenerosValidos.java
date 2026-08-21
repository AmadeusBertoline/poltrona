package poltrona.validation.generosValidos;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = GenerosValidosValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@NotNull
public @interface GenerosValidos {

    String message() default "Os gêneros devem ser informados corretamente.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}