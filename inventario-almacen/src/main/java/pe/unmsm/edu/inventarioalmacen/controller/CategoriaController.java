package pe.unmsm.edu.inventarioalmacen.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.unmsm.edu.inventarioalmacen.dto.CategoriaDTO;
import pe.unmsm.edu.inventarioalmacen.service.CategoriaService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> listarCategorias() {
        log.info("GET /categories");
        List<CategoriaDTO> categorias = categoriaService.listarCategorias();
        return ResponseEntity.ok(categorias);
    }

    @PostMapping
    public ResponseEntity<CategoriaDTO> crearCategoria(@Valid @RequestBody CategoriaDTO dto) {
        log.info("POST /categories - nombre: {}", dto.getNombre());
        CategoriaDTO created = categoriaService.crearCategoria(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaDTO> actualizarCategoria(
            @PathVariable UUID id,
            @Valid @RequestBody CategoriaDTO dto) {
        log.info("PUT /categories/{}", id);
        CategoriaDTO updated = categoriaService.actualizarCategoria(id, dto);
        return ResponseEntity.ok(updated);
    }
}
