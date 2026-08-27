package poltrona.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import poltrona.entity.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByCpf(String cpf);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

}
