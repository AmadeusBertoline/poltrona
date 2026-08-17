package poltrona.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import poltrona.entity.Operador;

public interface OperadorRepository extends JpaRepository<Operador, Long> {
    
    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);
}