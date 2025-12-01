package pe.unmsm.edu.inventarioalmacen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.unmsm.edu.inventarioalmacen.entity.CategoriaMaterial;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoriaMaterialRepository extends JpaRepository<CategoriaMaterial, UUID> {
    
    Optional<CategoriaMaterial> findByNombre(String nombre);
    
    List<CategoriaMaterial> findByActivoOrderByNombreAsc(boolean activo);
}
