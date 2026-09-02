package poltrona.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import poltrona.entity.Preco;

public interface PrecoRepository extends JpaRepository<Preco, Long> {

    boolean existsByNomeIgnoreCaseAndCinemaId(String nome, Long cinemaId);

    Page<Preco> findAllByCinemaId(Long cinemaId, Pageable pageable);

    Optional<Preco> findByIdAndCinemaProprietarioId(Long precoId, Long proprietarioId);

}
