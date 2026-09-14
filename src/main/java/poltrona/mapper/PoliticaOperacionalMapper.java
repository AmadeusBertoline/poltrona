package poltrona.mapper;

import org.springframework.stereotype.Component;
import poltrona.dto.politicaOperacional.PoliticaOperacionalRequestDTO;
import poltrona.dto.politicaOperacional.PoliticaOperacionalResponseDTO;
import poltrona.entity.PoliticaOperacional;

@Component
public class PoliticaOperacionalMapper {

    public PoliticaOperacional toEntity(PoliticaOperacionalRequestDTO dto) {

        if (dto == null) {
            return null;
        }

        return new PoliticaOperacional(dto.toleranciaMinutosCompra(), dto.antecedenciaMinutosCancelamento(),
                dto.intervaloLimpezaMinutos());

    }

    public PoliticaOperacionalResponseDTO toDTO(PoliticaOperacional politica) {

        if (politica == null) {
            return null;
        }

        return new PoliticaOperacionalResponseDTO(politica.getToleranciaMinutosCompra(),
                politica.getAntecedenciaMinutosCancelamento(), politica.getIntervaloLimpezaMinutos());

    }

}
