package pe.unmsm.edu.inventarioalmacen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.unmsm.edu.inventarioalmacen.entity.UnidadAlternativa;

import java.util.List;
import java.util.UUID;

@Repository
public interface UnidadAlternativaRepository extends JpaRepository<UnidadAlternativa, UUID> {
    
    List<UnidadAlternativa> findByMaterial_MaterialIdAndActivo(UUID materialId, boolean activo);
}
