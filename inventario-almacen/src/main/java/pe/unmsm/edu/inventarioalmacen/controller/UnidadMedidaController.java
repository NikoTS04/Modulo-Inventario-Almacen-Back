package pe.unmsm.edu.inventarioalmacen.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.unmsm.edu.inventarioalmacen.dto.UnidadDTO;
import pe.unmsm.edu.inventarioalmacen.service.UnidadMedidaService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/units")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class UnidadMedidaController {

    private final UnidadMedidaService unidadService;

    @GetMapping
    public ResponseEntity<List<UnidadDTO>> listarUnidades() {
        log.info("GET /units");
        List<UnidadDTO> unidades = unidadService.listarUnidades();
        return ResponseEntity.ok(unidades);
    }

    @PostMapping
    public ResponseEntity<UnidadDTO> crearUnidad(@Valid @RequestBody UnidadDTO dto) {
        log.info("POST /units - nombre: {}", dto.getNombre());
        UnidadDTO created = unidadService.crearUnidad(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UnidadDTO> actualizarUnidad(
            @PathVariable UUID id,
            @Valid @RequestBody UnidadDTO dto) {
        log.info("PUT /units/{}", id);
        UnidadDTO updated = unidadService.actualizarUnidad(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UnidadDTO> desactivarUnidad(@PathVariable UUID id) {
        log.info("DELETE /units/{} - Desactivando unidad", id);
        UnidadDTO desactivada = unidadService.desactivarUnidad(id);
        return ResponseEntity.ok(desactivada);
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<UnidadDTO> activarUnidad(@PathVariable UUID id) {
        log.info("POST /units/{}/activate - Activando unidad", id);
        UnidadDTO activada = unidadService.activarUnidad(id);
        return ResponseEntity.ok(activada);
    }

    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<Void> eliminarPermanente(@PathVariable UUID id) {
        log.info("DELETE /units/{}/permanent - Eliminando permanentemente", id);
        unidadService.eliminarPermanente(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/has-materials")
    public ResponseEntity<Boolean> tieneMateriales(@PathVariable UUID id) {
        log.info("GET /units/{}/has-materials", id);
        boolean hasMaterials = unidadService.tieneMateriales(id);
        return ResponseEntity.ok(hasMaterials);
    }
}
