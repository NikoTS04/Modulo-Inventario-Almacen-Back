package pe.unmsm.edu.inventarioalmacen.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.unmsm.edu.inventarioalmacen.dto.garantia.*;
import pe.unmsm.edu.inventarioalmacen.entity.enums.DestinoGarantia;
import pe.unmsm.edu.inventarioalmacen.entity.enums.EstadoGarantia;
import pe.unmsm.edu.inventarioalmacen.service.GarantiaService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/garantias")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class GarantiaController {

    private final GarantiaService garantiaService;

    /**
     * POST /api/v1/garantias
     * Registrar nueva garantía (acepta formato legacy)
     */
    @PostMapping
    public ResponseEntity<DevolucionDTO> registrarGarantia(@Valid @RequestBody DevolucionCreateDTO dto) {
        log.info("POST /api/v1/garantias - Registrando nueva devolución (legacy)");
        DevolucionDTO created = garantiaService.registrarDevolucion(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * GET /api/v1/garantias/{idGarantia}
     * Obtener detalle de una garantía (retorna formato legacy para compatibilidad)
     */
    @GetMapping("/{idGarantia}")
    public ResponseEntity<DevolucionDTO> obtenerGarantia(@PathVariable UUID idGarantia) {
        log.info("GET /api/v1/garantias/{}", idGarantia);
        DevolucionDTO garantia = garantiaService.obtenerDevolucion(idGarantia);
        return ResponseEntity.ok(garantia);
    }

    /**
     * POST /api/v1/garantias/{idGarantia}/inspeccion
     * Registrar resultado de inspección
     */
    @PostMapping("/{idGarantia}/inspeccion")
    public ResponseEntity<GarantiaDTO> registrarInspeccion(
            @PathVariable UUID idGarantia,
            @Valid @RequestBody InspeccionDTO dto) {
        log.info("POST /api/v1/garantias/{}/inspeccion", idGarantia);
        GarantiaDTO updated = garantiaService.registrarInspeccion(idGarantia, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * POST /api/v1/garantias/{idGarantia}/decision
     * Confirmar decisión final (acepta formato legacy)
     */
    @PostMapping("/{idGarantia}/decision")
    public ResponseEntity<DevolucionDTO> confirmarDecision(
            @PathVariable UUID idGarantia,
            @Valid @RequestBody ProcesarDevolucionDTO dto) {
        log.info("POST /api/v1/garantias/{}/decision - Procesando devolución (legacy)", idGarantia);
        DevolucionDTO updated = garantiaService.procesarDevolucion(idGarantia, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * GET /api/v1/garantias
     * Listar garantías con filtros (acepta search para formato legacy)
     */
    @GetMapping
    public ResponseEntity<GarantiaListResponseDTO> listarGarantias(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) EstadoGarantia estado,
            @RequestParam(required = false) DestinoGarantia destino,
            @RequestParam(required = false) String search) {
        log.info("GET /api/v1/garantias - page: {}, limit: {}, estado: {}, destino: {}, search: {}", 
                page, limit, estado, destino, search);
        
        // Si hay search, usar método legacy
        if (search != null && !search.trim().isEmpty()) {
            GarantiaListResponseDTO response = garantiaService.listarDevoluciones(page, limit, estado, search);
            return ResponseEntity.ok(response);
        }
        
        // Formato nuevo
        GarantiaListResponseDTO response = garantiaService.listarGarantias(page, limit, sort, estado, destino);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/garantias/logs
     * Listar historial de movimientos
     */
    @GetMapping("/logs")
    public ResponseEntity<GarantiaListResponseDTO> listarHistorial(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) UUID devolucionId) {
        log.info("GET /api/v1/garantias/logs - page: {}, limit: {}, tipo: {}, devolucionId: {}", 
                page, limit, tipo, devolucionId);
        GarantiaListResponseDTO response = garantiaService.listarHistorial(page, limit, tipo, devolucionId);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/garantias/{idGarantia}/iniciar-revision
     * Iniciar revisión de una garantía
     */
    @PostMapping("/{idGarantia}/iniciar-revision")
    public ResponseEntity<GarantiaDTO> iniciarRevision(@PathVariable UUID idGarantia) {
        log.info("POST /api/v1/garantias/{}/iniciar-revision", idGarantia);
        GarantiaDTO updated = garantiaService.iniciarRevision(idGarantia);
        return ResponseEntity.ok(updated);
    }

    /**
     * POST /api/v1/garantias/{idGarantia}/completar
     * Completar una garantía en reparación
     */
    @PostMapping("/{idGarantia}/completar")
    public ResponseEntity<GarantiaDTO> completarGarantia(@PathVariable UUID idGarantia) {
        log.info("POST /api/v1/garantias/{}/completar", idGarantia);
        GarantiaDTO updated = garantiaService.completarGarantia(idGarantia);
        return ResponseEntity.ok(updated);
    }

    /**
     * POST /api/v1/garantias/{idGarantia}/cancelar
     * Cancelar una garantía
     */
    @PostMapping("/{idGarantia}/cancelar")
    public ResponseEntity<GarantiaDTO> cancelarGarantia(@PathVariable UUID idGarantia) {
        log.info("POST /api/v1/garantias/{}/cancelar", idGarantia);
        GarantiaDTO updated = garantiaService.cancelarGarantia(idGarantia);
        return ResponseEntity.ok(updated);
    }
}

