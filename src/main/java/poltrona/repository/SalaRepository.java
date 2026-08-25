package poltrona.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import poltrona.entity.Sala;

public interface SalaRepository extends JpaRepository<Sala, Long> {

    boolean existsByCinemaId(Long idCinema);

}
