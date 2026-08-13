package poltrona.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import poltrona.entity.Cinema;

public interface CinemaRepository extends JpaRepository<Cinema, Long>{
    
}
