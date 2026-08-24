package poltrona.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import poltrona.entity.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);

    @Query("SELECT CASE WHEN COUNT(a) = 1 THEN true ELSE false END FROM Admin a WHERE a.ativo = true")
    boolean eUltimoAdmin();

}
