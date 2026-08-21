package poltrona.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import poltrona.entity.Ingresso;

public interface IngressoRepository extends JpaRepository<Ingresso, Long> {

    boolean existsBySessaoIdAndPoltronaId(Long sessaoId, Long poltronaId);

}
