package poltrona.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import poltrona.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("""
                SELECT u
                FROM Usuario u
                WHERE u.email = :login OR u.cpf = :login
            """)
    Optional<Usuario> findByEmailOrCpf(@Param("login") String login);

}
