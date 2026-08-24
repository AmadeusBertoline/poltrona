package poltrona.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import io.lettuce.core.dynamic.annotation.Param;
import poltrona.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("SELECT u FROM Usuario u WHERE u.cpf = :emailOrCpf OR u.email = :emailOrCpf")
    Optional<Usuario> findByEmailOrCpf(@Param("emailOrCpf") String emailOrCpf);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);
}
