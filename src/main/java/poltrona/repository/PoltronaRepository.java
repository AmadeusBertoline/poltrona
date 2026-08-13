package poltrona.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import poltrona.entity.Poltrona;

public interface PoltronaRepository extends JpaRepository<Poltrona, Long>{
    
}
