package pe.unmsm.edu.inventarioalmacen.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para importación de materiales desde archivo CSV/Excel
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialImportDTO {
    
    @NotBlank(message = "El código es obligatorio")
    private String codigo;
    
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    private String descripcion;
    
    @NotBlank(message = "La categoría es obligatoria")
    private String categoriaNombre;
    
    @NotBlank(message = "La unidad base es obligatoria")
    private String unidadBaseNombre;
    
    @NotNull(message = "El stock inicial es obligatorio")
    @Positive(message = "El stock inicial debe ser mayor a 0")
    private BigDecimal stockInicial;
    
    @NotNull(message = "El stock mínimo es obligatorio")
    @Positive(message = "El stock mínimo debe ser mayor a 0")
    private BigDecimal stockMinimo;
    
    @NotNull(message = "El punto de reorden es obligatorio")
    @Positive(message = "El punto de reorden debe ser mayor a 0")
    private BigDecimal puntoReorden;
    
    @Builder.Default
    private Boolean activarAlerta = true;
    
    @Builder.Default
    private Boolean activo = true;
}
