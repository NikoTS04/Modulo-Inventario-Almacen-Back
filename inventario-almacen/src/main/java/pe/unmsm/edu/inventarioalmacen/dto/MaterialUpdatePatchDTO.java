package pe.unmsm.edu.inventarioalmacen.dto;

import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialUpdatePatchDTO {
    private String nombre;
    private String descripcion;
    private UUID categoriaId;
    private UUID unidadBaseId;
    private Boolean activo;
}
