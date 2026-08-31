package poltrona.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import poltrona.entity.Usuario;
import poltrona.enums.usuario.StatusConta;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

        @Query("""
                            SELECT u
                            FROM Usuario u
                            WHERE (u.email = :login OR u.cpf = :login)
                            AND u.status = :status
                        """)
        Optional<Usuario> findByEmailOrCpfAndStatus(
                        @Param("login") String login,
                        @Param("status") StatusConta status);

        boolean existsByEmail(String email);

        boolean existsByCpf(String cpf);

        @Query("""
                            SELECT COUNT(u) > 0
                            FROM Usuario u
                            WHERE (u.email = :email)
                            AND u.status = :status
                        """)
        boolean existsByEmailAndStatus(
                        @Param("email") String email,
                        @Param("status") StatusConta status);

        @Query("""
                        SELECT COUNT(u) > 0
                        FROM Usuario u
                        WHERE (u.cpf = :cpf)
                        AND u.status = :status
                        """)
        boolean existsByCpfAndStatus(@Param("cpf") String cpf,
                        @Param("status") StatusConta status);

}
