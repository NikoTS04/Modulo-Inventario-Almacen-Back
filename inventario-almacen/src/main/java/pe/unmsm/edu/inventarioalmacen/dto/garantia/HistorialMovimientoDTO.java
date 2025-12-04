package pe.unmsm.edu.inventarioalmacen.dto.garantia;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para historial de movimientos de garantías
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HistorialMovimientoDTO {
    
    @JsonProperty("movimientoId")
    private UUID movimientoId;
    
    @JsonProperty("devolucionId")
    private UUID devolucionId;
    
    @JsonProperty("devolucionCodigo")
    private String devolucionCodigo;
    
    @JsonProperty("itemId")
    private UUID itemId;
    
    @JsonProperty("materialId")
    private UUID materialId;
    
    @JsonProperty("materialNombre")
    private String materialNombre;
    
    private String tipo; // REGISTRO, INSPECCION, REINTEGRO, REPARACION, ELIMINACION, COMPLETAR
    
    private java.math.BigDecimal cantidad;
    
    private String descripcion;
    
    private String usuario;
    
    private LocalDateTime fecha;
}

