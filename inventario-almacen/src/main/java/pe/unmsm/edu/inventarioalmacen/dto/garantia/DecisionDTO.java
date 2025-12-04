package pe.unmsm.edu.inventarioalmacen.dto.garantia;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import pe.unmsm.edu.inventarioalmacen.entity.enums.DestinoGarantia;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DecisionDTO {
    
    @NotNull(message = "El destino es requerido")
    private DestinoGarantia destino;
    
    @NotNull(message = "El ID del usuario responsable es requerido")
    private UUID usuarioResponsableId;
    
    @Size(max = 1000, message = "El comentario no puede exceder 1000 caracteres")
    private String comentario;
}

