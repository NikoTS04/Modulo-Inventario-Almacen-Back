package pe.unmsm.edu.inventarioalmacen.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MaterialDetailDTO {
    private UUID materialId;
    private String codigo;
    private String nombre;
    private String descripcion;
    private UUID categoriaId;
    private String categoriaNombre;
    private UUID unidadBaseId;
    private String unidadBaseNombre;
    private String unidadBaseSimbolo;
    private boolean activo;
    private BigDecimal stockDisponible;
    private BigDecimal stockComprometido;
    private BigDecimal stockTotal;
    private Boolean alertaReorden;
    private ReordenConfigDTO reordenConfig;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
    private String usuarioCreacion;
    private String usuarioModificacion;
}
