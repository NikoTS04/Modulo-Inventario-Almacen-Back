package pe.unmsm.edu.inventarioalmacen.dto.garantia;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import pe.unmsm.edu.inventarioalmacen.entity.enums.EstadoFisico;
import pe.unmsm.edu.inventarioalmacen.entity.enums.ResultadoInspeccion;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemGarantiaDTO {
    
    @JsonProperty("idItemGarantia")
    private UUID itemGarantiaId;
    
    @JsonProperty("idProducto")
    public UUID getIdProducto() {
        return materialId;
    }
    
    @JsonProperty("materialId")
    private UUID materialId;
    
    @JsonProperty("productoCodigo")
    public String getProductoCodigo() {
        return materialCodigo;
    }
    
    @JsonProperty("productoNombre")
    public String getProductoNombre() {
        return materialNombre;
    }
    
    private String materialCodigo;
    private String materialNombre;
    
    private BigDecimal cantidad;
    private String lote;
    
    @JsonProperty("motivoDevolucion")
    private String motivoDevolucion;
    
    // Alias para compatibilidad
    @JsonProperty("motivo")
    public String getMotivo() {
        return motivoDevolucion;
    }
    
    @JsonProperty("estadoFisico")
    private EstadoFisico estadoFisico;
    
    @JsonProperty("resultadoInspeccion")
    private ResultadoInspeccion resultadoInspeccion;
    
    private String observaciones;
    
    @JsonProperty("fechaCreacion")
    private LocalDateTime fechaCreacion;
}

