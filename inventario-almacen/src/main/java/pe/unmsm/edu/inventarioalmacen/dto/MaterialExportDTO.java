package pe.unmsm.edu.inventarioalmacen.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para exportación de materiales
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialExportDTO {
    
    private String codigo;
    private String nombre;
    private String descripcion;
    private String categoriaNombre;
    private String unidadBaseNombre;
    private BigDecimal stockActual;
    private BigDecimal stockMinimo;
    private BigDecimal puntoReorden;
    private Boolean activarAlerta;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private String usuarioCreacion;
}
