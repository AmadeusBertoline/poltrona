package poltrona.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import poltrona.entity.Preco;

public interface PrecoRepository extends JpaRepository<Preco, Long>{
    
    boolean existsByNome(String nome);

}
