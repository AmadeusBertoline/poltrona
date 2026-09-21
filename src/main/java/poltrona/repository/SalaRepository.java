package poltrona.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import poltrona.entity.Sala;

public interface SalaRepository extends JpaRepository<Sala, Long> {

        boolean existsByCinemaId(Long idCinema);

        boolean existsByCinemaIdAndNumero(Long idCinema, Integer numero);

        Page<Sala> findAllByCinemaId(Long cinemaId, Pageable pageable);

        boolean existsByCinemaIdAndCinemaProprietarioId(Long cinemaId, Long proprietarioId);

        Optional<Sala> findByIdAndCinemaProprietarioId(Long salaId, Long proprietarioId);

        @Query("""
                            SELECT s FROM Sala s
                            WHERE (:cinemaId IS NULL OR s.cinema.id = :cinemaId)
                              AND (:ativo IS NULL OR s.ativa = :ativo)
                        """)
        Page<Sala> buscarSalas(
                        @Param("cinemaId") Long cinemaId,
                        @Param("ativo") Boolean ativo,
                        Pageable pageable);

        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("""
                        SELECT s FROM Sala s
                        WHERE s.id = :id
                        """)
        Optional<Sala> findByIdWithLock(Long id);

}
