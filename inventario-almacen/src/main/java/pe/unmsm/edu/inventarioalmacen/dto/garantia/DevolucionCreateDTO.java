package pe.unmsm.edu.inventarioalmacen.dto.garantia;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;
import java.util.UUID;

/**
 * DTO Legacy para compatibilidad con frontend existente
 * POST /api/v1/garantias
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DevolucionCreateDTO {
    
    @Size(max = 200, message = "El nombre del cliente no puede exceder 200 caracteres")
    private String clienteNombre;
    
    @Size(max = 50, message = "El documento del cliente no puede exceder 50 caracteres")
    private String clienteDocumento;
    
    @Size(max = 1000, message = "El motivo general no puede exceder 1000 caracteres")
    private String motivoGeneral;
    
    @Size(max = 2000, message = "Las observaciones no pueden exceder 2000 caracteres")
    private String observaciones;
    
    @NotEmpty(message = "Debe incluir al menos un item")
    @Valid
    private List<ItemDevolucionCreateDTO> items;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemDevolucionCreateDTO {
        
        @jakarta.validation.constraints.NotNull(message = "El materialId es requerido")
        private UUID materialId;
        
        @jakarta.validation.constraints.NotNull(message = "La cantidad es requerida")
        @jakarta.validation.constraints.Positive(message = "La cantidad debe ser mayor a cero")
        private java.math.BigDecimal cantidad;
        
        @Size(max = 500, message = "El motivo no puede exceder 500 caracteres")
        private String motivo;
        
        @Size(max = 1000, message = "Las observaciones no pueden exceder 1000 caracteres")
        private String observaciones;
    }
}

