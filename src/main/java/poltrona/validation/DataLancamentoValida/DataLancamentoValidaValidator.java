package poltrona.validation.dataLancamentoValida;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class DataLancamentoValidaValidator implements ConstraintValidator<DataLancamentoValida, LocalDate> {
    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) return false;
        
        LocalDate dataMinima = LocalDate.of(1888, 1, 1);
        LocalDate dataMaxima = LocalDate.now().plusYears(5);
        
        return !value.isBefore(dataMinima) && !value.isAfter(dataMaxima);
    }
}