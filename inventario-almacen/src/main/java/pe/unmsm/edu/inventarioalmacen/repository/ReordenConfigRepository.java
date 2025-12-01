package pe.unmsm.edu.inventarioalmacen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.unmsm.edu.inventarioalmacen.entity.ReordenConfig;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReordenConfigRepository extends JpaRepository<ReordenConfig, UUID> {
    
    Optional<ReordenConfig> findByMaterial_MaterialId(UUID materialId);
}
