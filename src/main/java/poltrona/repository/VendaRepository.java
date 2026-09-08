package poltrona.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import poltrona.entity.Cliente;
import poltrona.entity.Venda;

public interface VendaRepository extends JpaRepository<Venda, Long> {

    Page<Venda> findByCliente(Cliente cliente, Pageable pageable);

}
