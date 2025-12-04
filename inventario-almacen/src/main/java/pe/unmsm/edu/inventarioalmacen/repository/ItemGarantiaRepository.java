package pe.unmsm.edu.inventarioalmacen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.unmsm.edu.inventarioalmacen.entity.ItemGarantia;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ItemGarantiaRepository extends JpaRepository<ItemGarantia, UUID> {
    
    @Query("SELECT i FROM ItemGarantia i WHERE i.garantia.garantiaId = :garantiaId")
    List<ItemGarantia> findByGarantiaId(@Param("garantiaId") UUID garantiaId);
    
    @Query("SELECT i FROM ItemGarantia i LEFT JOIN FETCH i.material WHERE i.itemGarantiaId = :id")
    Optional<ItemGarantia> findByIdWithMaterial(@Param("id") UUID id);
    
    @Query("SELECT i FROM ItemGarantia i WHERE i.material.materialId = :materialId")
    List<ItemGarantia> findByMaterialId(@Param("materialId") UUID materialId);
}

