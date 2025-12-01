package pe.unmsm.edu.inventarioalmacen.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.unmsm.edu.inventarioalmacen.dto.*;
import pe.unmsm.edu.inventarioalmacen.service.MaterialService;

import java.util.UUID;

@RestController
@RequestMapping("/materials")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class MaterialController {

    private final MaterialService materialService;

    @GetMapping
    public ResponseEntity<MaterialListResponseDTO> listarMateriales(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) UUID categoria,
            @RequestParam(required = false) Boolean activo) {
        
        log.info("GET /materials - page: {}, limit: {}", page, limit);
        MaterialListResponseDTO response = materialService.consultarMateriales(page, limit, sort, categoria, activo);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{materialId}")
    public ResponseEntity<MaterialDetailDTO> obtenerMaterial(@PathVariable UUID materialId) {
        log.info("GET /materials/{}", materialId);
        MaterialDetailDTO material = materialService.obtenerMaterial(materialId);
        return ResponseEntity.ok(material);
    }

    @PostMapping
    public ResponseEntity<MaterialDetailDTO> crearMaterial(@Valid @RequestBody MaterialCreateDTO dto) {
        log.info("POST /materials - código: {}", dto.getCodigo());
        MaterialDetailDTO created = materialService.crearMaterial(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{materialId}")
    public ResponseEntity<MaterialDetailDTO> actualizarMaterial(
            @PathVariable UUID materialId,
            @Valid @RequestBody MaterialCreateDTO dto) {
        log.info("PUT /materials/{}", materialId);
        MaterialDetailDTO updated = materialService.editarMaterial(materialId, dto);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{materialId}")
    public ResponseEntity<MaterialDetailDTO> actualizarMaterialParcial(
            @PathVariable UUID materialId,
            @RequestBody MaterialUpdatePatchDTO patch) {
        log.info("PATCH /materials/{}", materialId);
        MaterialDetailDTO updated = materialService.editarMaterialParcial(materialId, patch);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{materialId}")
    public ResponseEntity<Void> desactivarMaterial(@PathVariable UUID materialId) {
        log.info("DELETE /materials/{}", materialId);
        materialService.desactivarMaterial(materialId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{materialId}/activate")
    public ResponseEntity<MaterialDetailDTO> activarMaterial(@PathVariable UUID materialId) {
        log.info("POST /materials/{}/activate", materialId);
        MaterialDetailDTO activated = materialService.activarMaterial(materialId);
        return ResponseEntity.ok(activated);
    }

    @PutMapping("/{materialId}/reorder-config")
    public ResponseEntity<Void> definirConfiguracionReorden(
            @PathVariable UUID materialId,
            @Valid @RequestBody ReordenConfigDTO configDTO) {
        log.info("PUT /materials/{}/reorder-config", materialId);
        materialService.definirReorden(materialId, configDTO);
        return ResponseEntity.ok().build();
    }
}
