package poltrona.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import poltrona.entity.Venda;

public interface VendaRepository extends JpaRepository<Venda, Long>{
    
}
