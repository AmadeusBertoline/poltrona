package poltrona.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import poltrona.enums.filme.GeneroFilme;

@Component
public class GeneroFilmeConverter implements Converter<String, GeneroFilme> {

    @Override
    public GeneroFilme convert(String source) {
        return GeneroFilme.fromDescricao(source);
    }
}