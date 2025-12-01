package pe.unmsm.edu.inventarioalmacen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.unmsm.edu.inventarioalmacen.entity.Inventario;
import pe.unmsm.edu.inventarioalmacen.entity.Material;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, UUID> {
    
    Optional<Inventario> findByMaterial(Material material);
    
    Optional<Inventario> findByMaterial_MaterialId(UUID materialId);
}
