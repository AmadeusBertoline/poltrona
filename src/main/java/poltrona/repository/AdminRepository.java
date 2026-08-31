package poltrona.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import poltrona.entity.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long>{

    
} 