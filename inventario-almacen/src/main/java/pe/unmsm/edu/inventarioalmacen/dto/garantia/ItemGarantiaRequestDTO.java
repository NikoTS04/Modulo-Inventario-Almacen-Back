package pe.unmsm.edu.inventarioalmacen.dto.garantia;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemGarantiaRequestDTO {
    
    @NotNull(message = "El ID del producto es requerido")
    private UUID idProducto;
    
    @NotNull(message = "La cantidad es requerida")
    @Positive(message = "La cantidad debe ser mayor a cero")
    private BigDecimal cantidad;
    
    private String lote;
    
    @Size(max = 500, message = "El motivo de devolución no puede exceder 500 caracteres")
    private String motivoDevolucion;
    
    @Size(max = 1000, message = "Las observaciones no pueden exceder 1000 caracteres")
    private String observaciones;
}

