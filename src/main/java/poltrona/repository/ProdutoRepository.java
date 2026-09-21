package poltrona.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import poltrona.entity.Produto;
import poltrona.enums.produto.TipoProduto;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

        boolean existsByNomeIgnoreCaseAndCinemaId(String nome, Long cinemaId);

        Optional<Produto> findByIdAndCinemaProprietarioId(Long id, Long proprietarioId);

        @Query("""
                        SELECT p FROM Produto p
                        WHERE (:ativo IS NULL OR p.ativo = :ativo)
                        AND (:nome IS NULL OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
                        AND (:tipoProduto IS NULL OR p.tipo = :tipoProduto)
                        """)
        Page<Produto> findAllByFiltro(
                        @Param("ativo") Boolean ativo,
                        @Param("nome") String nome,
                        @Param("tipoProduto") TipoProduto tipoProduto,
                        Pageable pageable);

        @Modifying
        @Query("""
                        UPDATE Produto p
                        SET p.quantidadeEstoque = p.quantidadeEstoque - :quantidade
                        WHERE p.id = :id
                        AND p.quantidadeEstoque >= :quantidade
                                """)
        int reduzirEstoque(Long id, Integer quantidade);

}