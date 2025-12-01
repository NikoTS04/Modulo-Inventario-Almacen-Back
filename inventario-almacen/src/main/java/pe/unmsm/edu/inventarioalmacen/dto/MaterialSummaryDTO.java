package pe.unmsm.edu.inventarioalmacen.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialSummaryDTO {
    private UUID materialId;
    private String codigo;
    private String nombre;
    private String categoriaNombre;
    private String unidadBaseSimbolo;
    private boolean activo;
    private BigDecimal stockTotal;
    private Boolean alertaReorden;
}
