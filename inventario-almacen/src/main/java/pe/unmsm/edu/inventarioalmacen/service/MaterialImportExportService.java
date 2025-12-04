package pe.unmsm.edu.inventarioalmacen.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pe.unmsm.edu.inventarioalmacen.dto.*;
import pe.unmsm.edu.inventarioalmacen.entity.*;
import pe.unmsm.edu.inventarioalmacen.exception.ResourceNotFoundException;
import pe.unmsm.edu.inventarioalmacen.factory.MaterialFactory;
import pe.unmsm.edu.inventarioalmacen.mapper.MaterialMapper;
import pe.unmsm.edu.inventarioalmacen.repository.*;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Servicio para importación y exportación de materiales
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MaterialImportExportService {

    private final MaterialRepository materialRepository;
    private final CategoriaMaterialRepository categoriaRepository;
    private final UnidadMedidaRepository unidadRepository;
    private final InventarioRepository inventarioRepository;
    private final MaterialFactory materialFactory;
    private final MaterialMapper materialMapper;

    private static final String[] CSV_HEADERS = {
            "codigo", "nombre", "descripcion", "categoria", "unidad_base",
            "stock_inicial", "stock_minimo", "punto_reorden", "activar_alerta", "activo"
    };

    /**
     * Importa materiales desde un archivo CSV
     */
    public MaterialImportResponseDTO importarMateriales(MultipartFile file) {
        log.info("Iniciando importación de materiales desde archivo: {}", file.getOriginalFilename());

        MaterialImportResponseDTO response = MaterialImportResponseDTO.builder()
                .totalProcesados(0)
                .exitosos(0)
                .fallidos(0)
                .errores(new ArrayList<>())
                .materialesCreados(new ArrayList<>())
                .build();

        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                     .builder()
                     .setHeader(CSV_HEADERS)
                     .setSkipHeaderRecord(true)
                     .setIgnoreEmptyLines(true)
                     .setTrim(true)
                     .build())) {

            int lineNumber = 1; // Línea 1 es el encabezado
            for (CSVRecord record : csvParser) {
                lineNumber++;
                response.setTotalProcesados(response.getTotalProcesados() + 1);

                try {
                    MaterialImportDTO importDTO = parsearRegistroCSV(record);
                    MaterialDetailDTO creado = procesarImportacion(importDTO);
                    response.getMaterialesCreados().add(creado);
                    response.setExitosos(response.getExitosos() + 1);
                    log.info("Material importado exitosamente: {}", importDTO.getCodigo());
                } catch (Exception e) {
                    response.setFallidos(response.getFallidos() + 1);
                    response.agregarError(lineNumber, e.getMessage());
                    log.warn("Error al procesar línea {}: {}", lineNumber, e.getMessage());
                }
            }

        } catch (IOException e) {
            log.error("Error al leer archivo CSV", e);
            response.agregarError(0, "Error al leer el archivo: " + e.getMessage());
        }

        log.info("Importación completada. Exitosos: {}, Fallidos: {}", 
                response.getExitosos(), response.getFallidos());
        return response;
    }

    /**
     * Exporta materiales a CSV según filtros aplicados
     */
    @Transactional(readOnly = true)
    public byte[] exportarMateriales(String sort, UUID categoriaId, Boolean activo, String search) {
        log.info("Iniciando exportación de materiales con filtros - categoria: {}, activo: {}, búsqueda: '{}'",
                categoriaId, activo, search);

        // Obtener todos los materiales que coincidan con los filtros (sin paginación)
        Sort sortObj = Sort.by(Sort.Direction.ASC, sort != null ? sort : "nombre");
        Pageable pageable = PageRequest.of(0, 10000, sortObj); // Límite alto para exportar todos

        Page<Material> materialesPage;

        // Aplicar los mismos filtros que en la consulta normal
        if (search != null && !search.trim().isEmpty()) {
            String searchTerm = "%" + search.toLowerCase() + "%";
            if (categoriaId != null && activo != null) {
                materialesPage = materialRepository.findBySearchAndCategoriaAndActivo(
                        searchTerm, categoriaId, activo, pageable);
            } else if (categoriaId != null) {
                materialesPage = materialRepository.findBySearchAndCategoria(searchTerm, categoriaId, pageable);
            } else if (activo != null) {
                materialesPage = materialRepository.findBySearchAndActivo(searchTerm, activo, pageable);
            } else {
                materialesPage = materialRepository.findBySearch(searchTerm, pageable);
            }
        } else if (categoriaId != null && activo != null) {
            materialesPage = materialRepository.findByCategoriaIdAndActivo(categoriaId, activo, pageable);
        } else if (categoriaId != null) {
            materialesPage = materialRepository.findByCategoriaId(categoriaId, pageable);
        } else if (activo != null) {
            materialesPage = materialRepository.findByActivo(activo, pageable);
        } else {
            materialesPage = materialRepository.findAll(pageable);
        }

        List<Material> materiales = materialesPage.getContent();
        log.info("Exportando {} materiales", materiales.size());

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(CSV_HEADERS))) {

            for (Material material : materiales) {
                MaterialExportDTO exportDTO = convertirAExportDTO(material);
                csvPrinter.printRecord(
                        exportDTO.getCodigo(),
                        exportDTO.getNombre(),
                        exportDTO.getDescripcion(),
                        exportDTO.getCategoriaNombre(),
                        exportDTO.getUnidadBaseNombre(),
                        exportDTO.getStockActual(),
                        exportDTO.getStockMinimo(),
                        exportDTO.getPuntoReorden(),
                        exportDTO.getActivarAlerta(),
                        exportDTO.getActivo()
                );
            }

            csvPrinter.flush();
            writer.flush();
            log.info("Exportación completada exitosamente");
            return out.toByteArray();

        } catch (IOException e) {
            log.error("Error al generar archivo CSV de exportación", e);
            throw new RuntimeException("Error al generar archivo de exportación", e);
        }
    }

    /**
     * Parsea un registro CSV a MaterialImportDTO
     */
    private MaterialImportDTO parsearRegistroCSV(CSVRecord record) {
        return MaterialImportDTO.builder()
                .codigo(record.get("codigo"))
                .nombre(record.get("nombre"))
                .descripcion(record.get("descripcion"))
                .categoriaNombre(record.get("categoria"))
                .unidadBaseNombre(record.get("unidad_base"))
                .stockInicial(new BigDecimal(record.get("stock_inicial")))
                .stockMinimo(new BigDecimal(record.get("stock_minimo")))
                .puntoReorden(new BigDecimal(record.get("punto_reorden")))
                .activarAlerta(parsearBoolean(record.get("activar_alerta"), true))
                .activo(parsearBoolean(record.get("activo"), true))
                .build();
    }

    /**
     * Procesa la importación de un material individual
     */
    private MaterialDetailDTO procesarImportacion(MaterialImportDTO dto) {
        // Verificar que no exista el código
        if (materialRepository.findByCodigo(dto.getCodigo()).isPresent()) {
            throw new IllegalArgumentException(
                    String.format("Ya existe un material con el código '%s'", dto.getCodigo()));
        }

        // Buscar categoría por nombre
        CategoriaMaterial categoria = categoriaRepository.findByNombre(dto.getCategoriaNombre())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoría", "nombre", dto.getCategoriaNombre()));

        if (!categoria.isActivo()) {
            throw new IllegalArgumentException(
                    String.format("La categoría '%s' no está activa", dto.getCategoriaNombre()));
        }

        // Buscar unidad por nombre
        UnidadMedida unidad = unidadRepository.findByNombre(dto.getUnidadBaseNombre())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Unidad de medida", "nombre", dto.getUnidadBaseNombre()));

        if (!unidad.isActivo()) {
            throw new IllegalArgumentException(
                    String.format("La unidad '%s' no está activa", dto.getUnidadBaseNombre()));
        }

        // Crear MaterialCreateDTO para reutilizar lógica existente
        MaterialCreateDTO createDTO = MaterialCreateDTO.builder()
                .codigo(dto.getCodigo())
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .categoriaId(categoria.getCategoriaId())
                .unidadBaseId(unidad.getUnidadId())
                .reordenConfig(ReordenConfigDTO.builder()
                        .stockMinimo(dto.getStockMinimo())
                        .puntoReorden(dto.getPuntoReorden())
                        .activarAlerta(dto.getActivarAlerta())
                        .build())
                .build();

        // Crear material usando factory
        Material material = materialFactory.reconstruirDesdeDTO(createDTO, categoria, unidad);
        material.setActivo(dto.getActivo());
        material.setUsuarioCreacion("import-system");

        // Configurar reorden
        material.definirReordenConfig(dto.getStockMinimo(), dto.getPuntoReorden(), dto.getActivarAlerta());

        // Guardar material
        Material savedMaterial = materialRepository.save(material);

        // Crear inventario inicial
        Inventario inventario = Inventario.builder()
                .material(savedMaterial)
                .cantidadDisponible(dto.getStockInicial())
                .cantidadComprometida(BigDecimal.ZERO)
                .build();
        inventarioRepository.save(inventario);

        log.info("Material importado y inventario inicializado: {} con stock {}", 
                savedMaterial.getCodigo(), dto.getStockInicial());

        return materialMapper.toDetailDTO(savedMaterial);
    }

    /**
     * Convierte Material a MaterialExportDTO
     */
    private MaterialExportDTO convertirAExportDTO(Material material) {
        BigDecimal stockActual = BigDecimal.ZERO;
        BigDecimal stockMinimo = null;
        BigDecimal puntoReorden = null;
        Boolean activarAlerta = false;

        // Obtener datos de inventario si existe
        if (material.getInventario() != null) {
            // cantidadTotal es la suma de disponible + comprometida
            BigDecimal disponible = material.getInventario().getCantidadDisponible();
            BigDecimal comprometida = material.getInventario().getCantidadComprometida();
            stockActual = disponible.add(comprometida);
        }

        // Obtener datos de configuración de reorden si existe
        if (material.getReordenConfig() != null) {
            stockMinimo = material.getReordenConfig().getStockMinimo();
            puntoReorden = material.getReordenConfig().getPuntoReorden();
            activarAlerta = material.getReordenConfig().isActivarAlerta();
        }

        return MaterialExportDTO.builder()
                .codigo(material.getCodigo())
                .nombre(material.getNombre())
                .descripcion(material.getDescripcion())
                .categoriaNombre(material.getCategoria().getNombre())
                .unidadBaseNombre(material.getUnidadBase().getNombre())
                .stockActual(stockActual)
                .stockMinimo(stockMinimo)
                .puntoReorden(puntoReorden)
                .activarAlerta(activarAlerta)
                .activo(material.isActivo())
                .fechaCreacion(material.getFechaCreacion())
                .usuarioCreacion(material.getUsuarioCreacion())
                .build();
    }

    /**
     * Parsea un valor string a boolean
     */
    private Boolean parsearBoolean(String valor, Boolean defaultValue) {
        if (valor == null || valor.trim().isEmpty()) {
            return defaultValue;
        }
        String v = valor.trim().toLowerCase();
        return v.equals("true") || v.equals("1") || v.equals("si") || v.equals("sí") || v.equals("yes");
    }
}
