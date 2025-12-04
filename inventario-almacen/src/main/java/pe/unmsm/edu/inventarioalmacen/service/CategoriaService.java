package pe.unmsm.edu.inventarioalmacen.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.unmsm.edu.inventarioalmacen.dto.CategoriaDTO;
import pe.unmsm.edu.inventarioalmacen.entity.CategoriaMaterial;
import pe.unmsm.edu.inventarioalmacen.exception.DuplicateResourceException;
import pe.unmsm.edu.inventarioalmacen.exception.ResourceNotFoundException;
import pe.unmsm.edu.inventarioalmacen.mapper.MaterialMapper;
import pe.unmsm.edu.inventarioalmacen.repository.CategoriaMaterialRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoriaService {

    private final CategoriaMaterialRepository categoriaRepository;
    private final MaterialMapper mapper;

    @Transactional(readOnly = true)
    public List<CategoriaDTO> listarCategorias() {
        log.debug("Listando todas las categorías activas");
        return categoriaRepository.findByActivoOrderByNombreAsc(true).stream()
                .map(mapper::toCategoriaDTO)
                .collect(Collectors.toList());
    }

    public CategoriaDTO crearCategoria(CategoriaDTO dto) {
        log.info("Creando categoría: {}", dto.getNombre());
        
        if (categoriaRepository.findByNombre(dto.getNombre()).isPresent()) {
            throw new DuplicateResourceException("Categoría", "nombre", dto.getNombre());
        }
        
        CategoriaMaterial categoria = CategoriaMaterial.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .activo(dto.getActivo() != null ? dto.getActivo() : true)
                .build();
        
        CategoriaMaterial saved = categoriaRepository.save(categoria);
        log.info("Categoría creada con ID: {}", saved.getCategoriaId());
        
        return mapper.toCategoriaDTO(saved);
    }

    @Transactional(readOnly = true)
    public CategoriaDTO obtenerCategoria(UUID id) {
        log.debug("Obteniendo categoría ID: {}", id);
        CategoriaMaterial categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", "id", id));
        return mapper.toCategoriaDTO(categoria);
    }

    public CategoriaDTO actualizarCategoria(UUID id, CategoriaDTO dto) {
        log.info("Actualizando categoría ID: {}", id);
        
        CategoriaMaterial categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", "id", id));
        
        if (!categoria.getNombre().equals(dto.getNombre())) {
            if (categoriaRepository.findByNombre(dto.getNombre()).isPresent()) {
                throw new DuplicateResourceException("Categoría", "nombre", dto.getNombre());
            }
        }
        
        categoria.actualizar(dto.getNombre(), dto.getDescripcion());
        
        if (dto.getActivo() != null) {
            categoria.setActivo(dto.getActivo());
        }
        
        CategoriaMaterial updated = categoriaRepository.save(categoria);
        return mapper.toCategoriaDTO(updated);
    }
}
