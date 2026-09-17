package poltrona.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import poltrona.entity.Cliente;
import poltrona.entity.Venda;

public interface VendaRepository extends JpaRepository<Venda, Long> {

    Page<Venda> findByCliente(Cliente cliente, Pageable pageable);

    @Query("""
                SELECT v FROM Venda v
                WHERE (:clienteId IS NULL OR v.cliente.id = :clienteId)
            """)
    Page<Venda> buscarVendas(@Param("clienteId") Long clienteId, Pageable pageable);

}
