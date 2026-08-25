package poltrona.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import poltrona.entity.Proprietario;

public interface ProprietarioRepository extends JpaRepository<Proprietario, Long>{
    
}
