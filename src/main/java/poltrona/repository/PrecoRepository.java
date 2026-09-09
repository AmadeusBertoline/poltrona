package poltrona.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import poltrona.entity.Preco;
import poltrona.enums.filme.FormatoFilme;

public interface PrecoRepository extends JpaRepository<Preco, Long> {

    boolean existsByFormatoAndCinemaId(FormatoFilme formato, Long cinemaId);

    Page<Preco> findAllByCinemaId(Long cinemaId, Pageable pageable);

    Optional<Preco> findByIdAndCinemaProprietarioId(Long precoId, Long proprietarioId);

    Optional<Preco> findByIdAndAtivoTrue(Long id);

    Optional<Preco> findByCinemaIdAndFormato(Long cinemaId, FormatoFilme formato);

}
