package pe.unmsm.edu.inventarioalmacen.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.unmsm.edu.inventarioalmacen.dto.*;
import pe.unmsm.edu.inventarioalmacen.entity.*;
import pe.unmsm.edu.inventarioalmacen.exception.DuplicateResourceException;
import pe.unmsm.edu.inventarioalmacen.exception.ResourceNotFoundException;
import pe.unmsm.edu.inventarioalmacen.factory.MaterialFactory;
import pe.unmsm.edu.inventarioalmacen.mapper.MaterialMapper;
import pe.unmsm.edu.inventarioalmacen.repository.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final CategoriaMaterialRepository categoriaRepository;
    private final UnidadMedidaRepository unidadRepository;
    private final MaterialFactory materialFactory;
    private final MaterialMapper materialMapper;

    public MaterialDetailDTO crearMaterial(MaterialCreateDTO dto) {
        log.info("Creando material con código: {}", dto.getCodigo());
        
        // Verificar código único
        if (materialRepository.findByCodigo(dto.getCodigo()).isPresent()) {
            throw new DuplicateResourceException("Material", "codigo", dto.getCodigo());
        }
        
        // Obtener categoría
        CategoriaMaterial categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", "id", dto.getCategoriaId()));
        
        // Obtener unidad
        UnidadMedida unidad = unidadRepository.findById(dto.getUnidadBaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Unidad", "id", dto.getUnidadBaseId()));
        
        // Crear material usando factory
        Material material = materialFactory.reconstruirDesdeDTO(dto, categoria, unidad);
        material.setUsuarioCreacion("system"); // TODO: obtener del contexto de seguridad
        
        // Guardar
        Material saved = materialRepository.save(material);
        log.info("Material creado exitosamente con ID: {}", saved.getMaterialId());
        
        return materialMapper.toDetailDTO(saved);
    }

    public MaterialDetailDTO editarMaterial(UUID materialId, MaterialCreateDTO dto) {
        log.info("Editando material ID: {}", materialId);
        
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material", "id", materialId));
        
        // Verificar código único si cambió
        if (!material.getCodigo().equals(dto.getCodigo())) {
            if (materialRepository.findByCodigo(dto.getCodigo()).isPresent()) {
                throw new DuplicateResourceException("Material", "codigo", dto.getCodigo());
            }
            material.setCodigo(dto.getCodigo());
        }
        
        // Obtener categoría y unidad
        CategoriaMaterial categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", "id", dto.getCategoriaId()));
        
        UnidadMedida unidad = unidadRepository.findById(dto.getUnidadBaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Unidad", "id", dto.getUnidadBaseId()));
        
        // Actualizar datos
        material.actualizarDatos(dto.getNombre(), dto.getDescripcion(), categoria, unidad);
        material.setUsuarioModificacion("system"); // TODO: obtener del contexto de seguridad
        
        // Actualizar configuración de reorden si está presente
        if (dto.getReordenConfig() != null) {
            material.definirReordenConfig(
                    dto.getReordenConfig().getStockMinimo(),
                    dto.getReordenConfig().getPuntoReorden(),
                    dto.getReordenConfig().getActivarAlerta() != null ? dto.getReordenConfig().getActivarAlerta() : true
            );
        }
        
        Material updated = materialRepository.save(material);
        log.info("Material actualizado exitosamente");
        
        return materialMapper.toDetailDTO(updated);
    }

    public MaterialDetailDTO editarMaterialParcial(UUID materialId, MaterialUpdatePatchDTO patch) {
        log.info("Editando parcialmente material ID: {}", materialId);
        
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material", "id", materialId));
        
        if (patch.getNombre() != null) {
            material.setNombre(patch.getNombre());
        }
        
        if (patch.getDescripcion() != null) {
            material.setDescripcion(patch.getDescripcion());
        }
        
        if (patch.getCategoriaId() != null) {
            CategoriaMaterial categoria = categoriaRepository.findById(patch.getCategoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría", "id", patch.getCategoriaId()));
            material.setCategoria(categoria);
        }
        
        if (patch.getUnidadBaseId() != null) {
            UnidadMedida unidad = unidadRepository.findById(patch.getUnidadBaseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Unidad", "id", patch.getUnidadBaseId()));
            material.setUnidadBase(unidad);
        }
        
        if (patch.getActivo() != null) {
            material.setActivo(patch.getActivo());
        }
        
        material.setUsuarioModificacion("system");
        
        Material updated = materialRepository.save(material);
        return materialMapper.toDetailDTO(updated);
    }

    public void desactivarMaterial(UUID materialId) {
        log.info("Desactivando material ID: {}", materialId);
        
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material", "id", materialId));
        
        material.desactivar();
        material.setUsuarioModificacion("system");
        materialRepository.save(material);
        
        log.info("Material desactivado exitosamente");
    }

    public MaterialDetailDTO activarMaterial(UUID materialId) {
        log.info("Activando material ID: {}", materialId);
        
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material", "id", materialId));
        
        material.activar();
        material.setUsuarioModificacion("system");
        Material updated = materialRepository.save(material);
        
        log.info("Material activado exitosamente");
        return materialMapper.toDetailDTO(updated);
    }

    public void definirReorden(UUID materialId, ReordenConfigDTO configDTO) {
        log.info("Definiendo configuración de reorden para material ID: {}", materialId);
        
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material", "id", materialId));
        
        material.definirReordenConfig(
                configDTO.getStockMinimo(),
                configDTO.getPuntoReorden(),
                configDTO.getActivarAlerta() != null ? configDTO.getActivarAlerta() : true
        );
        
        materialRepository.save(material);
        log.info("Configuración de reorden actualizada exitosamente");
    }

    @Transactional(readOnly = true)
    public MaterialDetailDTO obtenerMaterial(UUID materialId) {
        log.debug("Obteniendo material ID: {}", materialId);
        
        Material material = materialRepository.findByIdWithDetails(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material", "id", materialId));
        
        return materialMapper.toDetailDTO(material);
    }

    @Transactional(readOnly = true)
    public MaterialListResponseDTO consultarMateriales(int page, int limit, String sortBy, UUID categoriaId, Boolean activo) {
        log.debug("Consultando materiales - página: {}, límite: {}", page, limit);
        
        Sort sort = Sort.by(Sort.Direction.ASC, sortBy != null ? sortBy : "nombre");
        Pageable pageable = PageRequest.of(page, limit, sort);
        
        Page<Material> materialesPage;
        
        if (categoriaId != null && activo != null) {
            materialesPage = materialRepository.findByCategoriaIdAndActivo(categoriaId, activo, pageable);
        } else {
            materialesPage = materialRepository.findAll(pageable);
        }
        
        List<MaterialSummaryDTO> items = materialesPage.getContent().stream()
                .map(materialMapper::toSummaryDTO)
                .collect(Collectors.toList());
        
        return MaterialListResponseDTO.builder()
                .items(items)
                .page(page)
                .limit(limit)
                .total(materialesPage.getTotalElements())
                .totalPages(materialesPage.getTotalPages())
                .build();
    }

    public MaterialDetailDTO definirReordenConfig(UUID materialId, ReordenConfigDTO configDTO) {
        log.info("Definiendo configuración de reorden para material {}", materialId);
        
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material", "id", materialId));
        
        material.definirReordenConfig(
                configDTO.getStockMinimo(),
                configDTO.getPuntoReorden(),
                configDTO.getActivarAlerta() != null ? configDTO.getActivarAlerta() : true
        );
        
        Material updated = materialRepository.save(material);
        log.info("Configuración de reorden actualizada exitosamente");
        
        return materialMapper.toDetailDTO(updated);
    }
}
