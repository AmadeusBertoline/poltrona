package poltrona.mapper;

import org.springframework.stereotype.Component;
import poltrona.dto.produto.CadastroProdutoRequestDTO;
import poltrona.dto.produto.ProdutoResponseDTO;
import poltrona.entity.Cinema;
import poltrona.entity.Produto;

@Component
public class ProdutoMapper {

    public Produto toEntity(CadastroProdutoRequestDTO dto, Cinema cinema) {

        return new Produto(cinema, dto.nome(), dto.descricao(), dto.tipo(), dto.preco(), dto.quantidadeEstoque());

    }

    public ProdutoResponseDTO toDTO(Produto produto) {

        return new ProdutoResponseDTO(
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getQuantidadeEstoque(),
                produto.getAtivo());

    }

}
