package poltrona.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import poltrona.entity.Sala;

public interface SalaRepository extends JpaRepository<Sala, Long> {

    boolean existsByCinemaId(Long idCinema);

    boolean existsByCinemaIdAndNumero(Long idCinema, Integer numero);

    Page<Sala> findAllByCinemaId(Long cinemaId, Pageable pageable);

    boolean existsByCinemaIdAndCinemaProprietarioId(Long cinemaId, Long proprietarioId);

    Optional<Sala> findByIdAndCinemaProprietarioId(Long salaId, Long proprietarioId);

}
