package poltrona.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import poltrona.entity.Endereco;

public interface EnderecoRepository extends JpaRepository<Endereco, Long>{
    
}
