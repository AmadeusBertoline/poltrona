package poltrona.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import poltrona.entity.Gerente;

public interface GerenteRepository extends JpaRepository<Gerente, Long> {

    boolean existsByCpf(String cpf);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

}
