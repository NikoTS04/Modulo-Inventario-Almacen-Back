package pe.unmsm.edu.inventarioalmacen.dto.garantia;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InspeccionDTO {
    
    @NotNull(message = "El ID del inspector es requerido")
    private UUID inspectorId;
    
    @Size(max = 2000, message = "Las observaciones no pueden exceder 2000 caracteres")
    private String observaciones;
    
    @NotEmpty(message = "Debe incluir al menos un item inspeccionado")
    @Valid
    private List<ItemInspeccionDTO> items;
}

