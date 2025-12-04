package pe.unmsm.edu.inventarioalmacen.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.unmsm.edu.inventarioalmacen.entity.Garantia;
import pe.unmsm.edu.inventarioalmacen.entity.enums.DestinoGarantia;
import pe.unmsm.edu.inventarioalmacen.entity.enums.EstadoGarantia;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GarantiaRepository extends JpaRepository<Garantia, UUID> {
    
    Optional<Garantia> findByCodigo(String codigo);
    
    @Query("SELECT g FROM Garantia g LEFT JOIN FETCH g.items WHERE g.garantiaId = :id")
    Optional<Garantia> findByIdWithItems(@Param("id") UUID id);
    
    @Query("SELECT g FROM Garantia g WHERE g.estado = :estado")
    Page<Garantia> findByEstado(@Param("estado") EstadoGarantia estado, Pageable pageable);
    
    @Query("SELECT g FROM Garantia g WHERE g.destino = :destino")
    Page<Garantia> findByDestino(@Param("destino") DestinoGarantia destino, Pageable pageable);
    
    @Query("SELECT g FROM Garantia g WHERE g.estado = :estado AND g.destino = :destino")
    Page<Garantia> findByEstadoAndDestino(
            @Param("estado") EstadoGarantia estado, 
            @Param("destino") DestinoGarantia destino, 
            Pageable pageable);
    
    @Query("SELECT g FROM Garantia g WHERE g.idDevolucion = :idDevolucion")
    Page<Garantia> findByIdDevolucion(@Param("idDevolucion") UUID idDevolucion, Pageable pageable);
    
    @Query("SELECT DISTINCT g FROM Garantia g LEFT JOIN FETCH g.items i LEFT JOIN FETCH i.material WHERE g.garantiaId = :id")
    Optional<Garantia> findByIdWithFullDetails(@Param("id") UUID id);
    
    @Query("SELECT g FROM Garantia g WHERE LOWER(g.codigo) LIKE :search OR LOWER(g.nombreCliente) LIKE :search OR LOWER(g.motivo) LIKE :search")
    Page<Garantia> findBySearch(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT g FROM Garantia g WHERE g.estado = :estado AND (LOWER(g.codigo) LIKE :search OR LOWER(g.nombreCliente) LIKE :search OR LOWER(g.motivo) LIKE :search)")
    Page<Garantia> findByEstadoAndSearch(@Param("estado") EstadoGarantia estado, @Param("search") String search, Pageable pageable);
}

