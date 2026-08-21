package poltrona.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import poltrona.entity.Poltrona;

public interface PoltronaRepository extends JpaRepository<Poltrona, Long> {

    List<Poltrona> findBySalaId(Long id);

}
