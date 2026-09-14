package poltrona.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import poltrona.entity.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    boolean existsByNomeIgnoreCaseAndCinemaId(String nome, Long cinemaId);

    Optional<Produto> findByIdAndCinemaProprietarioId(Long id, Long proprietarioId);

}