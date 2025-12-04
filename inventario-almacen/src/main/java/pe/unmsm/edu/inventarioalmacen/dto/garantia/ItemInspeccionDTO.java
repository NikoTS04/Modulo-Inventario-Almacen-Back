package pe.unmsm.edu.inventarioalmacen.dto.garantia;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import pe.unmsm.edu.inventarioalmacen.entity.enums.EstadoFisico;
import pe.unmsm.edu.inventarioalmacen.entity.enums.ResultadoInspeccion;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemInspeccionDTO {
    
    @NotNull(message = "El ID del item de garantía es requerido")
    private UUID idItemGarantia;
    
    @NotNull(message = "El estado físico es requerido")
    private EstadoFisico estadoFisico;
    
    @NotNull(message = "El resultado de inspección es requerido")
    private ResultadoInspeccion resultadoInspeccion;
}

