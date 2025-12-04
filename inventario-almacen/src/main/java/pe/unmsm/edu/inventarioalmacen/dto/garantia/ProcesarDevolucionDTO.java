package pe.unmsm.edu.inventarioalmacen.dto.garantia;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import pe.unmsm.edu.inventarioalmacen.entity.enums.DestinoGarantia;

import java.util.List;
import java.util.UUID;

/**
 * DTO Legacy para procesar devolución (inspección + decisión)
 * POST /api/v1/garantias/{id}/decision
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcesarDevolucionDTO {
    
    @NotNull(message = "El devolucionId es requerido")
    private UUID devolucionId;
    
    @NotEmpty(message = "Debe incluir al menos un item")
    @Valid
    private List<InspeccionItemDTO> items;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InspeccionItemDTO {
        
        @NotNull(message = "El itemId es requerido")
        private UUID itemId;
        
        @NotNull(message = "El resultado es requerido")
        private ResultadoInspeccionLegacy resultado; // APTO, DAÑADO, NO_RECUPERABLE
        
        @Size(max = 1000, message = "Las observaciones no pueden exceder 1000 caracteres")
        private String observaciones;
        
        @NotNull(message = "El destino es requerido")
        private DestinoGarantia destino;
    }
    
    public enum ResultadoInspeccionLegacy {
        APTO,
        DAÑADO,
        NO_RECUPERABLE
    }
}

