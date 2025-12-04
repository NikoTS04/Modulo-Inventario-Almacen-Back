package pe.unmsm.edu.inventarioalmacen.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.unmsm.edu.inventarioalmacen.entity.Material;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MaterialRepository extends JpaRepository<Material, UUID> {
    
    Optional<Material> findByCodigoAndActivo(String codigo, boolean activo);
    
    Optional<Material> findByCodigo(String codigo);
    
    @Query("SELECT m FROM Material m WHERE m.categoria.categoriaId = :categoriaId AND m.activo = :activo")
    Page<Material> findByCategoriaIdAndActivo(@Param("categoriaId") UUID categoriaId, @Param("activo") boolean activo, Pageable pageable);
    
    @Query("SELECT m FROM Material m WHERE m.categoria.categoriaId = :categoriaId")
    Page<Material> findByCategoriaId(@Param("categoriaId") UUID categoriaId, Pageable pageable);
    
    @Query("SELECT m FROM Material m WHERE m.activo = :activo")
    Page<Material> findByActivo(@Param("activo") boolean activo, Pageable pageable);
    
    @Query("SELECT m FROM Material m WHERE LOWER(m.codigo) LIKE :search OR LOWER(m.nombre) LIKE :search")
    Page<Material> findBySearch(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT m FROM Material m WHERE (LOWER(m.codigo) LIKE :search OR LOWER(m.nombre) LIKE :search) AND m.activo = :activo")
    Page<Material> findBySearchAndActivo(@Param("search") String search, @Param("activo") boolean activo, Pageable pageable);
    
    @Query("SELECT m FROM Material m WHERE (LOWER(m.codigo) LIKE :search OR LOWER(m.nombre) LIKE :search) AND m.categoria.categoriaId = :categoriaId")
    Page<Material> findBySearchAndCategoria(@Param("search") String search, @Param("categoriaId") UUID categoriaId, Pageable pageable);
    
    @Query("SELECT m FROM Material m WHERE (LOWER(m.codigo) LIKE :search OR LOWER(m.nombre) LIKE :search) AND m.categoria.categoriaId = :categoriaId AND m.activo = :activo")
    Page<Material> findBySearchAndCategoriaAndActivo(@Param("search") String search, @Param("categoriaId") UUID categoriaId, @Param("activo") boolean activo, Pageable pageable);
    
    @Query("SELECT m FROM Material m WHERE LOWER(m.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Material> findByNombreContaining(@Param("nombre") String nombre);
    
    @Query("SELECT m FROM Material m LEFT JOIN FETCH m.categoria LEFT JOIN FETCH m.unidadBase WHERE m.materialId = :id")
    Optional<Material> findByIdWithDetails(@Param("id") UUID id);

    // Métodos para verificar relaciones antes de eliminar
    long countByCategoriaCategoriaId(UUID categoriaId);

    long countByUnidadBaseUnidadId(UUID unidadId);
}
