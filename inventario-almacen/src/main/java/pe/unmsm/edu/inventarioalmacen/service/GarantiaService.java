package pe.unmsm.edu.inventarioalmacen.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.unmsm.edu.inventarioalmacen.dto.garantia.*;
import pe.unmsm.edu.inventarioalmacen.entity.Garantia;
import pe.unmsm.edu.inventarioalmacen.entity.ItemGarantia;
import pe.unmsm.edu.inventarioalmacen.entity.Material;
import pe.unmsm.edu.inventarioalmacen.entity.enums.DestinoGarantia;
import pe.unmsm.edu.inventarioalmacen.entity.enums.EstadoGarantia;
import pe.unmsm.edu.inventarioalmacen.exception.ResourceNotFoundException;
import pe.unmsm.edu.inventarioalmacen.mapper.GarantiaMapper;
import pe.unmsm.edu.inventarioalmacen.entity.Inventario;
import pe.unmsm.edu.inventarioalmacen.entity.enums.EstadoFisico;
import pe.unmsm.edu.inventarioalmacen.entity.enums.ResultadoInspeccion;
import pe.unmsm.edu.inventarioalmacen.repository.GarantiaRepository;
import pe.unmsm.edu.inventarioalmacen.repository.InventarioRepository;
import pe.unmsm.edu.inventarioalmacen.repository.ItemGarantiaRepository;
import pe.unmsm.edu.inventarioalmacen.repository.MaterialRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GarantiaService {

    private final GarantiaRepository garantiaRepository;
    private final ItemGarantiaRepository itemGarantiaRepository;
    private final MaterialRepository materialRepository;
    private final InventarioRepository inventarioRepository;
    private final GarantiaMapper garantiaMapper;

    /**
     * Registrar una nueva garantía
     */
    public GarantiaDTO registrarGarantia(RegistrarGarantiaDTO dto) {
        log.info("Registrando nueva garantía con {} items", dto.getItems().size());

        // Crear la garantía
        Garantia garantia = Garantia.builder()
                .idDevolucion(dto.getIdDevolucion())
                .nombreCliente(dto.getNombreCliente())
                .documentoNit(dto.getDocumentoNit())
                .motivo(dto.getMotivo())
                .observacionesGenerales(dto.getObservacionesGenerales())
                .estado(EstadoGarantia.RECIBIDO)
                .usuarioCreacion("system") // TODO: obtener del contexto de seguridad
                .build();

        // Procesar los items
        for (ItemGarantiaRequestDTO itemDTO : dto.getItems()) {
            Material material = materialRepository.findById(itemDTO.getIdProducto())
                    .orElseThrow(() -> new ResourceNotFoundException("Material", "id", itemDTO.getIdProducto()));

            ItemGarantia item = ItemGarantia.builder()
                    .material(material)
                    .cantidad(itemDTO.getCantidad())
                    .lote(itemDTO.getLote())
                    .motivoDevolucion(itemDTO.getMotivoDevolucion())
                    .observaciones(itemDTO.getObservaciones())
                    .build();

            garantia.agregarItem(item);
        }

        Garantia saved = garantiaRepository.save(garantia);
        log.info("Garantía registrada exitosamente con ID: {} y código: {}", saved.getGarantiaId(), saved.getCodigo());

        return garantiaMapper.toDTO(saved);
    }

    /**
     * Obtener detalle de una garantía por ID
     */
    @Transactional(readOnly = true)
    public GarantiaDTO obtenerGarantia(UUID garantiaId) {
        log.debug("Obteniendo garantía ID: {}", garantiaId);

        Garantia garantia = garantiaRepository.findByIdWithFullDetails(garantiaId)
                .orElseThrow(() -> new ResourceNotFoundException("Garantía", "id", garantiaId));

        return garantiaMapper.toDTO(garantia);
    }

    /**
     * Registrar resultado de inspección
     */
    public GarantiaDTO registrarInspeccion(UUID garantiaId, InspeccionDTO dto) {
        log.info("Registrando inspección para garantía ID: {}", garantiaId);

        Garantia garantia = garantiaRepository.findByIdWithItems(garantiaId)
                .orElseThrow(() -> new ResourceNotFoundException("Garantía", "id", garantiaId));

        // Validar que la garantía esté en estado válido para inspección
        if (garantia.getEstado() != EstadoGarantia.RECIBIDO && garantia.getEstado() != EstadoGarantia.EN_REVISION) {
            throw new IllegalStateException("La garantía no está en un estado válido para inspección. Estado actual: " + garantia.getEstado());
        }

        // Crear un mapa de items por ID para búsqueda rápida
        Map<UUID, ItemGarantia> itemsMap = garantia.getItems().stream()
                .collect(Collectors.toMap(ItemGarantia::getItemGarantiaId, Function.identity()));

        // Procesar cada item de inspección
        for (ItemInspeccionDTO itemInspeccion : dto.getItems()) {
            ItemGarantia item = itemsMap.get(itemInspeccion.getIdItemGarantia());
            if (item == null) {
                throw new ResourceNotFoundException("ItemGarantía", "id", itemInspeccion.getIdItemGarantia());
            }
            item.registrarResultadoInspeccion(itemInspeccion.getEstadoFisico(), itemInspeccion.getResultadoInspeccion());
        }

        // Registrar la inspección en la garantía
        garantia.registrarInspeccion(dto.getInspectorId(), dto.getObservaciones());
        garantia.setUsuarioModificacion("system");

        Garantia updated = garantiaRepository.save(garantia);
        log.info("Inspección registrada exitosamente para garantía ID: {}", garantiaId);

        return garantiaMapper.toDTO(updated);
    }

    /**
     * Confirmar decisión final de la garantía
     */
    public GarantiaDTO confirmarDecision(UUID garantiaId, DecisionDTO dto) {
        log.info("Confirmando decisión para garantía ID: {}, destino: {}", garantiaId, dto.getDestino());

        Garantia garantia = garantiaRepository.findByIdWithItems(garantiaId)
                .orElseThrow(() -> new ResourceNotFoundException("Garantía", "id", garantiaId));

        // Validar que la garantía esté en estado pendiente de decisión
        if (garantia.getEstado() != EstadoGarantia.PENDIENTE_DECISION) {
            throw new IllegalStateException("La garantía no está pendiente de decisión. Estado actual: " + garantia.getEstado());
        }

        // Confirmar la decisión
        garantia.confirmarDecision(dto.getDestino(), dto.getUsuarioResponsableId(), dto.getComentario());
        garantia.setUsuarioModificacion("system");

        Garantia updated = garantiaRepository.save(garantia);
        log.info("Decisión confirmada exitosamente para garantía ID: {}", garantiaId);

        return garantiaMapper.toDTO(updated);
    }

    /**
     * Listar garantías con filtros opcionales
     */
    @Transactional(readOnly = true)
    public GarantiaListResponseDTO listarGarantias(int page, int limit, String sortBy,
                                                    EstadoGarantia estado, DestinoGarantia destino) {
        log.debug("Listando garantías - página: {}, límite: {}, estado: {}, destino: {}", page, limit, estado, destino);

        Sort sort = Sort.by(Sort.Direction.DESC, sortBy != null ? sortBy : "fechaCreacion");
        Pageable pageable = PageRequest.of(page, limit, sort);

        Page<Garantia> garantiasPage;

        if (estado != null && destino != null) {
            garantiasPage = garantiaRepository.findByEstadoAndDestino(estado, destino, pageable);
        } else if (estado != null) {
            garantiasPage = garantiaRepository.findByEstado(estado, pageable);
        } else if (destino != null) {
            garantiasPage = garantiaRepository.findByDestino(destino, pageable);
        } else {
            garantiasPage = garantiaRepository.findAll(pageable);
        }

        List<GarantiaDTO> items = garantiasPage.getContent().stream()
                .map(garantiaMapper::toSummaryDTO)
                .collect(Collectors.toList());

        return GarantiaListResponseDTO.builder()
                .items(items)
                .page(page)
                .limit(limit)
                .total(garantiasPage.getTotalElements())
                .totalPages(garantiasPage.getTotalPages())
                .build();
    }

    /**
     * Iniciar revisión de una garantía
     */
    public GarantiaDTO iniciarRevision(UUID garantiaId) {
        log.info("Iniciando revisión para garantía ID: {}", garantiaId);

        Garantia garantia = garantiaRepository.findById(garantiaId)
                .orElseThrow(() -> new ResourceNotFoundException("Garantía", "id", garantiaId));

        if (garantia.getEstado() != EstadoGarantia.RECIBIDO) {
            throw new IllegalStateException("Solo se puede iniciar revisión en garantías recibidas. Estado actual: " + garantia.getEstado());
        }

        garantia.iniciarRevision();
        garantia.setUsuarioModificacion("system");

        Garantia updated = garantiaRepository.save(garantia);
        return garantiaMapper.toDTO(updated);
    }

    /**
     * Completar una garantía en reparación
     */
    public GarantiaDTO completarGarantia(UUID garantiaId) {
        log.info("Completando garantía ID: {}", garantiaId);

        Garantia garantia = garantiaRepository.findById(garantiaId)
                .orElseThrow(() -> new ResourceNotFoundException("Garantía", "id", garantiaId));

        if (garantia.getEstado() != EstadoGarantia.EN_REPARACION) {
            throw new IllegalStateException("Solo se pueden completar garantías en reparación. Estado actual: " + garantia.getEstado());
        }

        garantia.completar();
        garantia.setUsuarioModificacion("system");

        Garantia updated = garantiaRepository.save(garantia);
        return garantiaMapper.toDTO(updated);
    }

    /**
     * Cancelar una garantía
     */
    public GarantiaDTO cancelarGarantia(UUID garantiaId) {
        log.info("Cancelando garantía ID: {}", garantiaId);

        Garantia garantia = garantiaRepository.findById(garantiaId)
                .orElseThrow(() -> new ResourceNotFoundException("Garantía", "id", garantiaId));

        if (garantia.getEstado() == EstadoGarantia.COMPLETADO || garantia.getEstado() == EstadoGarantia.CANCELADO) {
            throw new IllegalStateException("No se puede cancelar una garantía completada o ya cancelada. Estado actual: " + garantia.getEstado());
        }

        garantia.cancelar();
        garantia.setUsuarioModificacion("system");

        Garantia updated = garantiaRepository.save(garantia);
        return garantiaMapper.toDTO(updated);
    }

    // ========== MÉTODOS LEGACY PARA COMPATIBILIDAD CON FRONTEND ==========

    /**
     * Registrar devolución (formato legacy)
     */
    public DevolucionDTO registrarDevolucion(DevolucionCreateDTO dto) {
        log.info("Registrando nueva devolución (legacy) con {} items", dto.getItems().size());

        Garantia garantia = Garantia.builder()
                .nombreCliente(dto.getClienteNombre())
                .documentoNit(dto.getClienteDocumento())
                .motivo(dto.getMotivoGeneral())
                .observacionesGenerales(dto.getObservaciones())
                .estado(EstadoGarantia.RECIBIDO)
                .usuarioCreacion("system")
                .build();

        for (DevolucionCreateDTO.ItemDevolucionCreateDTO itemDTO : dto.getItems()) {
            Material material = materialRepository.findById(itemDTO.getMaterialId())
                    .orElseThrow(() -> new ResourceNotFoundException("Material", "id", itemDTO.getMaterialId()));

            ItemGarantia item = ItemGarantia.builder()
                    .material(material)
                    .cantidad(itemDTO.getCantidad())
                    .motivoDevolucion(itemDTO.getMotivo())
                    .observaciones(itemDTO.getObservaciones())
                    .build();

            garantia.agregarItem(item);
        }

        Garantia saved = garantiaRepository.save(garantia);
        log.info("Devolución registrada exitosamente con ID: {} y código: {}", saved.getGarantiaId(), saved.getCodigo());

        return garantiaMapper.toDevolucionDTO(saved);
    }

    /**
     * Obtener devolución (formato legacy)
     */
    @Transactional(readOnly = true)
    public DevolucionDTO obtenerDevolucion(UUID devolucionId) {
        log.debug("Obteniendo devolución (legacy) ID: {}", devolucionId);

        Garantia garantia = garantiaRepository.findByIdWithFullDetails(devolucionId)
                .orElseThrow(() -> new ResourceNotFoundException("Devolución", "id", devolucionId));

        return garantiaMapper.toDevolucionDTO(garantia);
    }

    /**
     * Procesar devolución (inspección + decisión en un solo paso)
     */
    public DevolucionDTO procesarDevolucion(UUID devolucionId, ProcesarDevolucionDTO dto) {
        log.info("Procesando devolución ID: {}", devolucionId);

        Garantia garantia = garantiaRepository.findByIdWithItems(devolucionId)
                .orElseThrow(() -> new ResourceNotFoundException("Devolución", "id", devolucionId));

        if (garantia.getEstado() != EstadoGarantia.RECIBIDO && garantia.getEstado() != EstadoGarantia.EN_REVISION) {
            throw new IllegalStateException("La devolución no está en un estado válido para procesar. Estado actual: " + garantia.getEstado());
        }

        Map<UUID, ItemGarantia> itemsMap = garantia.getItems().stream()
                .collect(Collectors.toMap(ItemGarantia::getItemGarantiaId, Function.identity()));

        DestinoGarantia destinoComun = null;
        boolean todosMismoDestino = true;

        // Procesar cada item
        for (ProcesarDevolucionDTO.InspeccionItemDTO itemDTO : dto.getItems()) {
            ItemGarantia item = itemsMap.get(itemDTO.getItemId());
            if (item == null) {
                throw new ResourceNotFoundException("ItemGarantía", "id", itemDTO.getItemId());
            }

            // Convertir resultado legacy a enums
            EstadoFisico estadoFisico = convertirResultadoLegacyAEstadoFisico(itemDTO.getResultado());
            ResultadoInspeccion resultadoInspeccion = convertirResultadoLegacyAResultadoInspeccion(itemDTO.getResultado(), itemDTO.getDestino());

            item.registrarResultadoInspeccion(estadoFisico, resultadoInspeccion);
            item.setObservaciones(itemDTO.getObservaciones());

            // Verificar si todos tienen el mismo destino
            if (destinoComun == null) {
                destinoComun = itemDTO.getDestino();
            } else if (!destinoComun.equals(itemDTO.getDestino())) {
                todosMismoDestino = false;
            }

            // Actualizar stock según destino
            actualizarStockPorDestino(item.getMaterial(), item.getCantidad(), itemDTO.getDestino());
        }

        // Si todos los items tienen el mismo destino, actualizar estado de la garantía
        if (todosMismoDestino && destinoComun != null) {
            garantia.confirmarDecision(destinoComun, UUID.randomUUID(), "Procesado desde frontend legacy");
        } else {
            garantia.registrarInspeccion(UUID.randomUUID(), "Inspección procesada");
            garantia.setEstado(EstadoGarantia.PENDIENTE_DECISION);
        }

        garantia.setUsuarioModificacion("system");
        Garantia updated = garantiaRepository.save(garantia);

        log.info("Devolución procesada exitosamente");
        return garantiaMapper.toDevolucionDTO(updated);
    }

    /**
     * Listar devoluciones con búsqueda (formato legacy)
     */
    @Transactional(readOnly = true)
    public GarantiaListResponseDTO listarDevoluciones(int page, int limit, EstadoGarantia estado, String search) {
        log.debug("Listando devoluciones (legacy) - página: {}, límite: {}, estado: {}, search: {}", page, limit, estado, search);

        Sort sort = Sort.by(Sort.Direction.DESC, "fechaCreacion");
        Pageable pageable = PageRequest.of(page, limit, sort);

        Page<Garantia> garantiasPage;

        if (search != null && !search.trim().isEmpty()) {
            String searchTerm = "%" + search.toLowerCase() + "%";
            if (estado != null) {
                garantiasPage = garantiaRepository.findByEstadoAndSearch(estado, searchTerm, pageable);
            } else {
                garantiasPage = garantiaRepository.findBySearch(searchTerm, pageable);
            }
        } else if (estado != null) {
            garantiasPage = garantiaRepository.findByEstado(estado, pageable);
        } else {
            garantiasPage = garantiaRepository.findAll(pageable);
        }

        List<GarantiaDTO> items = garantiasPage.getContent().stream()
                .map(garantiaMapper::toSummaryDTO)
                .collect(Collectors.toList());

        return GarantiaListResponseDTO.builder()
                .items(items)
                .page(page)
                .limit(limit)
                .total(garantiasPage.getTotalElements())
                .totalPages(garantiasPage.getTotalPages())
                .build();
    }

    /**
     * Listar historial de movimientos
     */
    @Transactional(readOnly = true)
    public GarantiaListResponseDTO listarHistorial(int page, int limit, String tipo, UUID devolucionId) {
        // TODO: Implementar tabla de historial si es necesario
        // Por ahora retornamos lista vacía
        return GarantiaListResponseDTO.builder()
                .items(new ArrayList<>())
                .page(page)
                .limit(limit)
                .total(0)
                .totalPages(0)
                .build();
    }

    // ========== MÉTODOS AUXILIARES ==========

    private EstadoFisico convertirResultadoLegacyAEstadoFisico(ProcesarDevolucionDTO.ResultadoInspeccionLegacy resultado) {
        return switch (resultado) {
            case APTO -> EstadoFisico.BUENO;
            case DAÑADO -> EstadoFisico.DANIADO;
            case NO_RECUPERABLE -> EstadoFisico.NO_RECUPERABLE;
        };
    }

    private ResultadoInspeccion convertirResultadoLegacyAResultadoInspeccion(
            ProcesarDevolucionDTO.ResultadoInspeccionLegacy resultado, DestinoGarantia destino) {
        if (resultado == ProcesarDevolucionDTO.ResultadoInspeccionLegacy.APTO) {
            return ResultadoInspeccion.APTO_REINTEGRO;
        } else if (destino == DestinoGarantia.REPARACION) {
            return ResultadoInspeccion.REPARABLE;
        } else {
            return ResultadoInspeccion.NO_REPARABLE;
        }
    }

    private void actualizarStockPorDestino(Material material, BigDecimal cantidad, DestinoGarantia destino) {
        Inventario inventario = inventarioRepository.findByMaterial_MaterialId(material.getMaterialId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventario", "materialId", material.getMaterialId()));

        switch (destino) {
            case REINTEGRO:
                // Incrementar stock disponible
                inventario.setCantidadDisponible(inventario.getCantidadDisponible().add(cantidad));
                break;
            case ELIMINACION:
                // Decrementar stock (ya estaba comprometido o disponible)
                if (inventario.getCantidadDisponible().compareTo(cantidad) >= 0) {
                    inventario.setCantidadDisponible(inventario.getCantidadDisponible().subtract(cantidad));
                } else {
                    BigDecimal resto = cantidad.subtract(inventario.getCantidadDisponible());
                    inventario.setCantidadDisponible(BigDecimal.ZERO);
                    inventario.setCantidadComprometida(inventario.getCantidadComprometida().subtract(resto));
                }
                break;
            case REPARACION:
                // Excluir temporalmente del stock disponible (mover a comprometido)
                if (inventario.getCantidadDisponible().compareTo(cantidad) >= 0) {
                    inventario.setCantidadDisponible(inventario.getCantidadDisponible().subtract(cantidad));
                    inventario.setCantidadComprometida(inventario.getCantidadComprometida().add(cantidad));
                }
                break;
        }

        inventarioRepository.save(inventario);
        log.debug("Stock actualizado para material {}: destino={}, cantidad={}", material.getMaterialId(), destino, cantidad);
    }
}

