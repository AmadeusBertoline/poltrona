package poltrona.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import poltrona.entity.Poltrona;

public interface PoltronaRepository extends JpaRepository<Poltrona, Long> {

    List<Poltrona> findBySalaId(Long id);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("SELECT p FROM Poltrona p WHERE p.id = :id")
    Optional<Poltrona> findByIdWithLockRead(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Poltrona p WHERE p.id = :id")
    Optional<Poltrona> findByIdWithLock(@Param("id") Long id);
}
