package poltrona.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import io.lettuce.core.dynamic.annotation.Param;
import poltrona.entity.Cliente;
import poltrona.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("SELECT c FROM Cliente c WHERE c.cpf = :emailOrCpf OR c.email = :emailOrCpf")
    Optional<Cliente> findByEmailOrCpf(@Param("emailOrCpf") String emailOrCpf);
}
