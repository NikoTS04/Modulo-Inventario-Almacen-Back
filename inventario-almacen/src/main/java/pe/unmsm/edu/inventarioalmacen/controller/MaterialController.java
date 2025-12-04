package pe.unmsm.edu.inventarioalmacen.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pe.unmsm.edu.inventarioalmacen.dto.*;
import pe.unmsm.edu.inventarioalmacen.facade.MaterialFacade;
import pe.unmsm.edu.inventarioalmacen.service.MaterialImportExportService;

import java.util.UUID;

@RestController
@RequestMapping("/materials")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class MaterialController {

    private final MaterialFacade materialFacade;
    private final MaterialImportExportService importExportService;

    @GetMapping
    public ResponseEntity<MaterialListResponseDTO> listarMateriales(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) UUID categoria,
            @RequestParam(required = false) Boolean activo,
            @RequestParam(required = false) String search) {
        
        log.info("GET /materials - page: {}, limit: {}, search: {}, categoria: {}, activo: {}", 
                page, limit, search, categoria, activo);
        MaterialListResponseDTO response = materialFacade.consultarMateriales(
                page, limit, sort, categoria, activo, search);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/import")
    public ResponseEntity<MaterialImportResponseDTO> importarMateriales(
            @RequestParam("file") MultipartFile file) {
        log.info("POST /materials/import - archivo: {}", file.getOriginalFilename());
        
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacío");
        }
        
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".csv")) {
            throw new IllegalArgumentException("Solo se permiten archivos CSV");
        }
        
        MaterialImportResponseDTO response = importExportService.importarMateriales(file);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportarMateriales(
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) UUID categoria,
            @RequestParam(required = false) Boolean activo,
            @RequestParam(required = false) String search) {
        
        log.info("GET /materials/export - sort: {}, categoria: {}, activo: {}, search: '{}'",
                sort, categoria, activo, search);
        
        byte[] csvData = importExportService.exportarMateriales(sort, categoria, activo, search);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "materiales_export.csv");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(csvData);
    }

    @GetMapping("/{materialId}")
    public ResponseEntity<MaterialDetailDTO> obtenerMaterial(@PathVariable UUID materialId) {
        log.info("GET /materials/{}", materialId);
        MaterialDetailDTO material = materialFacade.obtenerMaterial(materialId);
        return ResponseEntity.ok(material);
    }

    @PostMapping
    public ResponseEntity<MaterialDetailDTO> crearMaterial(@Valid @RequestBody MaterialCreateDTO dto) {
        log.info("POST /materials - código: {}", dto.getCodigo());
        MaterialDetailDTO created = materialFacade.crearMaterial(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{materialId}")
    public ResponseEntity<MaterialDetailDTO> actualizarMaterial(
            @PathVariable UUID materialId,
            @Valid @RequestBody MaterialCreateDTO dto) {
        log.info("PUT /materials/{}", materialId);
        MaterialDetailDTO updated = materialFacade.editarMaterial(materialId, dto);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{materialId}")
    public ResponseEntity<MaterialDetailDTO> actualizarMaterialParcial(
            @PathVariable UUID materialId,
            @RequestBody MaterialUpdatePatchDTO patch) {
        log.info("PATCH /materials/{}", materialId);
        MaterialDetailDTO updated = materialFacade.editarMaterialParcial(materialId, patch);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{materialId}")
    public ResponseEntity<Void> desactivarMaterial(@PathVariable UUID materialId) {
        log.info("DELETE /materials/{}", materialId);
        materialFacade.desactivarMaterial(materialId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{materialId}/activate")
    public ResponseEntity<MaterialDetailDTO> activarMaterial(@PathVariable UUID materialId) {
        log.info("POST /materials/{}/activate", materialId);
        MaterialDetailDTO activated = materialFacade.activarMaterial(materialId);
        return ResponseEntity.ok(activated);
    }

    @PutMapping("/{materialId}/reorder-config")
    public ResponseEntity<MaterialDetailDTO> definirConfiguracionReorden(
            @PathVariable UUID materialId,
            @Valid @RequestBody ReordenConfigDTO configDTO) {
        log.info("PUT /materials/{}/reorder-config", materialId);
        MaterialDetailDTO updated = materialFacade.definirReordenConfig(materialId, configDTO);
        return ResponseEntity.ok(updated);
    }
}
