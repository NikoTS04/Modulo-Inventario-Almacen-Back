package pe.unmsm.edu.inventarioalmacen.dto.garantia;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import pe.unmsm.edu.inventarioalmacen.entity.enums.DestinoGarantia;
import pe.unmsm.edu.inventarioalmacen.entity.enums.EstadoGarantia;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO Legacy para respuesta de devolución/garantía
 * Compatible con el formato esperado por el frontend
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DevolucionDTO {
    
    @JsonProperty("devolucionId")
    private UUID devolucionId;
    
    private String codigo;
    
    @JsonProperty("fechaRegistro")
    private LocalDateTime fechaRegistro;
    
    @JsonProperty("fechaActualizacion")
    private LocalDateTime fechaActualizacion;
    
    private EstadoGarantia estado;
    
    @JsonProperty("clienteNombre")
    private String clienteNombre;
    
    @JsonProperty("clienteDocumento")
    private String clienteDocumento;
    
    @JsonProperty("motivoGeneral")
    private String motivoGeneral;
    
    @JsonProperty("observaciones")
    private String observaciones;
    
    private List<ItemDevolucionDTO> items;
    
    @JsonProperty("usuarioRegistro")
    private String usuarioRegistro;
    
    @JsonProperty("usuarioActualizacion")
    private String usuarioActualizacion;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ItemDevolucionDTO {
        
        @JsonProperty("itemId")
        private UUID itemId;
        
        @JsonProperty("materialId")
        private UUID materialId;
        
        @JsonProperty("materialCodigo")
        private String materialCodigo;
        
        @JsonProperty("materialNombre")
        private String materialNombre;
        
        private java.math.BigDecimal cantidad;
        
        private String motivo;
        
        private String observaciones;
        
        private String estado; // PENDIENTE, INSPECCIONADO, REINTEGRADO, EN_REPARACION, ELIMINADO
        
        private DestinoGarantia destino;
        
        @JsonProperty("fechaInspeccion")
        private LocalDateTime fechaInspeccion;
        
        @JsonProperty("resultadoInspeccion")
        private String resultadoInspeccion;
    }
}

