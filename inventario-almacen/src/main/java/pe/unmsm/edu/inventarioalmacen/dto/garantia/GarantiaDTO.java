package pe.unmsm.edu.inventarioalmacen.dto.garantia;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import pe.unmsm.edu.inventarioalmacen.entity.enums.DestinoGarantia;
import pe.unmsm.edu.inventarioalmacen.entity.enums.EstadoGarantia;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GarantiaDTO {
    
    @JsonProperty("idGarantia")
    private UUID garantiaId;
    
    private String codigo;
    
    @JsonProperty("idDevolucion")
    private UUID idDevolucion;
    
    @JsonProperty("nombreCliente")
    private String nombreCliente;
    
    @JsonProperty("documentoNit")
    private String documentoNit;
    
    private String motivo;
    
    @JsonProperty("observacionesGenerales")
    private String observacionesGenerales;
    
    // Alias para compatibilidad con frontend
    @JsonProperty("observaciones")
    public String getObservaciones() {
        return observacionesGenerales;
    }
    
    private EstadoGarantia estado;
    private DestinoGarantia destino;
    
    // Información de inspección
    @JsonProperty("inspectorId")
    private UUID inspectorId;
    
    @JsonProperty("inspectorNombre")
    private String inspectorNombre; // TODO: Obtener del servicio de usuarios
    
    @JsonProperty("observacionesInspeccion")
    private String observacionesInspeccion;
    
    @JsonProperty("fechaInspeccion")
    private LocalDateTime fechaInspeccion;
    
    // Información de decisión
    @JsonProperty("usuarioResponsableId")
    private UUID usuarioResponsableId;
    
    @JsonProperty("comentarioDecision")
    private String comentarioDecision;
    
    @JsonProperty("fechaDecision")
    private LocalDateTime fechaDecision;
    
    // Items
    private List<ItemGarantiaDTO> items;
    
    @JsonProperty("totalItems")
    private Integer totalItems;
    
    // Auditoría - con alias para frontend
    @JsonProperty("fechaRegistro")
    public LocalDateTime getFechaRegistro() {
        return fechaCreacion;
    }
    
    @JsonProperty("fechaActualizacion")
    public LocalDateTime getFechaActualizacion() {
        return fechaModificacion;
    }
    
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
    
    @JsonProperty("usuarioRegistro")
    public String getUsuarioRegistro() {
        return usuarioCreacion;
    }
    
    @JsonProperty("usuarioActualizacion")
    public String getUsuarioActualizacion() {
        return usuarioModificacion;
    }
    
    private String usuarioCreacion;
    private String usuarioModificacion;
}

