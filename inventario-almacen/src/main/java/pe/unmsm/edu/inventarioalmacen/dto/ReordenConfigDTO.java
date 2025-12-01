package pe.unmsm.edu.inventarioalmacen.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReordenConfigDTO {
    
    @NotNull(message = "El stock mínimo es requerido")
    @DecimalMin(value = "0.0", message = "El stock mínimo debe ser mayor o igual a 0")
    private BigDecimal stockMinimo;
    
    @NotNull(message = "El punto de reorden es requerido")
    @DecimalMin(value = "0.0", message = "El punto de reorden debe ser mayor o igual a 0")
    private BigDecimal puntoReorden;
    
    private Boolean activarAlerta;
}
