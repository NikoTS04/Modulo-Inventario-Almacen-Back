package pe.unmsm.edu.inventarioalmacen.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.unmsm.edu.inventarioalmacen.dto.UnidadDTO;
import pe.unmsm.edu.inventarioalmacen.entity.UnidadMedida;
import pe.unmsm.edu.inventarioalmacen.exception.DuplicateResourceException;
import pe.unmsm.edu.inventarioalmacen.exception.ResourceNotFoundException;
import pe.unmsm.edu.inventarioalmacen.mapper.MaterialMapper;
import pe.unmsm.edu.inventarioalmacen.repository.UnidadMedidaRepository;
import pe.unmsm.edu.inventarioalmacen.repository.MaterialRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UnidadMedidaService {

    private final UnidadMedidaRepository unidadRepository;
    private final MaterialRepository materialRepository;
    private final MaterialMapper mapper;

    @Transactional(readOnly = true)
    public List<UnidadDTO> listarUnidades() {
        log.debug("Listando todas las unidades activas");
        return unidadRepository.findByActivoOrderByNombreAsc(true).stream()
                .map(mapper::toUnidadDTO)
                .collect(Collectors.toList());
    }

    public UnidadDTO crearUnidad(UnidadDTO dto) {
        log.info("Creando unidad: {}", dto.getNombre());
        
        if (unidadRepository.findByNombre(dto.getNombre()).isPresent()) {
            throw new DuplicateResourceException("Unidad", "nombre", dto.getNombre());
        }
        
        if (unidadRepository.findBySimbolo(dto.getSimbolo()).isPresent()) {
            throw new DuplicateResourceException("Unidad", "símbolo", dto.getSimbolo());
        }
        
        UnidadMedida unidad = UnidadMedida.builder()
                .nombre(dto.getNombre())
                .simbolo(dto.getSimbolo())
                .tipo(dto.getTipo())
                .activo(dto.getActivo() != null ? dto.getActivo() : true)
                .build();
        
        UnidadMedida saved = unidadRepository.save(unidad);
        log.info("Unidad creada con ID: {}", saved.getUnidadId());
        
        return mapper.toUnidadDTO(saved);
    }

    @Transactional(readOnly = true)
    public UnidadDTO obtenerUnidad(UUID id) {
        log.debug("Obteniendo unidad ID: {}", id);
        UnidadMedida unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidad", "id", id));
        return mapper.toUnidadDTO(unidad);
    }

    public UnidadDTO actualizarUnidad(UUID id, UnidadDTO dto) {
        log.info("Actualizando unidad ID: {}", id);
        
        UnidadMedida unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidad", "id", id));
        
        if (!unidad.getNombre().equals(dto.getNombre())) {
            if (unidadRepository.findByNombre(dto.getNombre()).isPresent()) {
                throw new DuplicateResourceException("Unidad", "nombre", dto.getNombre());
            }
            unidad.setNombre(dto.getNombre());
        }
        
        if (!unidad.getSimbolo().equals(dto.getSimbolo())) {
            if (unidadRepository.findBySimbolo(dto.getSimbolo()).isPresent()) {
                throw new DuplicateResourceException("Unidad", "símbolo", dto.getSimbolo());
            }
            unidad.setSimbolo(dto.getSimbolo());
        }
        
        unidad.setTipo(dto.getTipo());
        
        if (dto.getActivo() != null) {
            unidad.setActivo(dto.getActivo());
        }
        
        UnidadMedida updated = unidadRepository.save(unidad);
        return mapper.toUnidadDTO(updated);
    }

    public UnidadDTO desactivarUnidad(UUID id) {
        log.info("Desactivando unidad ID: {}", id);
        
        UnidadMedida unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidad", "id", id));
        
        unidad.setActivo(false);
        UnidadMedida updated = unidadRepository.save(unidad);
        
        log.info("Unidad desactivada: {}", updated.getNombre());
        return mapper.toUnidadDTO(updated);
    }

    public UnidadDTO activarUnidad(UUID id) {
        log.info("Activando unidad ID: {}", id);
        
        UnidadMedida unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidad", "id", id));
        
        unidad.setActivo(true);
        UnidadMedida updated = unidadRepository.save(unidad);
        
        log.info("Unidad activada: {}", updated.getNombre());
        return mapper.toUnidadDTO(updated);
    }

    public void eliminarPermanente(UUID id) {
        log.info("Eliminando permanentemente unidad ID: {}", id);
        
        UnidadMedida unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidad", "id", id));
        
        // Verificar que no tenga materiales asociados
        long materialesCount = materialRepository.countByUnidadBaseUnidadId(id);
        if (materialesCount > 0) {
            throw new IllegalStateException(
                String.format("No se puede eliminar la unidad '%s' porque tiene %d materiales asignados", 
                    unidad.getNombre(), materialesCount)
            );
        }
        
        unidadRepository.delete(unidad);
        log.info("Unidad eliminada permanentemente: {}", unidad.getNombre());
    }

    @Transactional(readOnly = true)
    public boolean tieneMateriales(UUID unidadId) {
        log.debug("Verificando si unidad ID: {} tiene materiales", unidadId);
        
        if (!unidadRepository.existsById(unidadId)) {
            throw new ResourceNotFoundException("Unidad", "id", unidadId);
        }
        
        long count = materialRepository.countByUnidadBaseUnidadId(unidadId);
        log.debug("Unidad ID: {} tiene {} materiales", unidadId, count);
        
        return count > 0;
    }
}
