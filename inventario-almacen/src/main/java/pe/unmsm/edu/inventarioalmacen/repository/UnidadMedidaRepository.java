package pe.unmsm.edu.inventarioalmacen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.unmsm.edu.inventarioalmacen.entity.UnidadMedida;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UnidadMedidaRepository extends JpaRepository<UnidadMedida, UUID> {
    
    Optional<UnidadMedida> findByNombre(String nombre);
    
    Optional<UnidadMedida> findBySimbolo(String simbolo);
    
    List<UnidadMedida> findByActivoOrderByNombreAsc(boolean activo);
}
