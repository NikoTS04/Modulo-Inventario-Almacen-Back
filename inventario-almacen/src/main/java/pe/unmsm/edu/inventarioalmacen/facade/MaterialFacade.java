package pe.unmsm.edu.inventarioalmacen.facade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.unmsm.edu.inventarioalmacen.dto.*;
import pe.unmsm.edu.inventarioalmacen.service.CategoriaService;
import pe.unmsm.edu.inventarioalmacen.service.MaterialService;
import pe.unmsm.edu.inventarioalmacen.service.UnidadMedidaService;

import java.util.UUID;

/**
 * Facade para operaciones de Material.
 * Proporciona una interfaz simplificada para el controller,
 * orquestando llamadas a múltiples servicios y asegurando transaccionalidad.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MaterialFacade {

    private final MaterialService materialService;
    private final CategoriaService categoriaService;
    private final UnidadMedidaService unidadMedidaService;

    /**
     * Consulta materiales con filtros, delegando a MaterialService
     */
    public MaterialListResponseDTO consultarMateriales(int page, int limit, String sort, 
                                                       UUID categoria, Boolean activo) {
        log.debug("Facade: Consultando materiales - page: {}, limit: {}", page, limit);
        return materialService.consultarMateriales(page, limit, sort, categoria, activo);
    }

    /**
     * Obtiene detalle de un material por ID
     */
    @Transactional(readOnly = true)
    public MaterialDetailDTO obtenerMaterial(UUID materialId) {
        log.debug("Facade: Obteniendo material {}", materialId);
        return materialService.obtenerMaterial(materialId);
    }

    /**
     * Crea un nuevo material, validando existencia de categoría y unidad
     */
    public MaterialDetailDTO crearMaterial(MaterialCreateDTO dto) {
        log.info("Facade: Creando material - código: {}", dto.getCodigo());
        
        // Validar que la categoría existe y está activa
        CategoriaDTO categoria = categoriaService.obtenerCategoria(dto.getCategoriaId());
        if (!categoria.getActivo()) {
            throw new IllegalArgumentException("La categoría seleccionada no está activa");
        }
        
        // Validar que la unidad existe y está activa
        UnidadDTO unidad = unidadMedidaService.obtenerUnidad(dto.getUnidadBaseId());
        if (!unidad.getActivo()) {
            throw new IllegalArgumentException("La unidad de medida seleccionada no está activa");
        }
        
        // Delegar creación al servicio
        return materialService.crearMaterial(dto);
    }

    /**
     * Edita un material existente (actualización completa)
     */
    public MaterialDetailDTO editarMaterial(UUID materialId, MaterialCreateDTO dto) {
        log.info("Facade: Editando material {}", materialId);
        
        // Validar que categoría y unidad existen si se están cambiando
        categoriaService.obtenerCategoria(dto.getCategoriaId());
        unidadMedidaService.obtenerUnidad(dto.getUnidadBaseId());
        
        return materialService.editarMaterial(materialId, dto);
    }

    /**
     * Edita parcialmente un material
     */
    public MaterialDetailDTO editarMaterialParcial(UUID materialId, MaterialUpdatePatchDTO patch) {
        log.info("Facade: Editando parcialmente material {}", materialId);
        
        // Validar categoría si se está actualizando
        if (patch.getCategoriaId() != null) {
            categoriaService.obtenerCategoria(patch.getCategoriaId());
        }
        
        // Validar unidad si se está actualizando
        if (patch.getUnidadBaseId() != null) {
            unidadMedidaService.obtenerUnidad(patch.getUnidadBaseId());
        }
        
        return materialService.editarMaterialParcial(materialId, patch);
    }

    /**
     * Desactiva un material
     */
    public void desactivarMaterial(UUID materialId) {
        log.info("Facade: Desactivando material {}", materialId);
        materialService.desactivarMaterial(materialId);
    }

    /**
     * Activa un material previamente desactivado
     */
    public MaterialDetailDTO activarMaterial(UUID materialId) {
        log.info("Facade: Activando material {}", materialId);
        return materialService.activarMaterial(materialId);
    }

    /**
     * Define o actualiza la configuración de reorden para un material
     */
    public MaterialDetailDTO definirReordenConfig(UUID materialId, ReordenConfigDTO configDTO) {
        log.info("Facade: Definiendo configuración de reorden para material {}", materialId);
        
        // Validar que el material existe
        obtenerMaterial(materialId);
        
        // Delegar al servicio
        return materialService.definirReordenConfig(materialId, configDTO);
    }
}
