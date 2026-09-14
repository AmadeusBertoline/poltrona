package poltrona.validation.formaPagamentoValida;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import poltrona.enums.venda.FormaPagamento;

public class FormaPagamentoValidaValidator 
        implements ConstraintValidator<FormaPagamentoValida, FormaPagamento> {

    @Override
    public boolean isValid(FormaPagamento value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return true;
    }
}