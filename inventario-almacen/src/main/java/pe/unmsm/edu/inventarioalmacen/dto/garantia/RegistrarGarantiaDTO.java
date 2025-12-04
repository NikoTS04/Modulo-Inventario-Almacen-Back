package pe.unmsm.edu.inventarioalmacen.dto.garantia;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarGarantiaDTO {
    
    private UUID idDevolucion;
    
    // Datos del cliente (opcional)
    @Size(max = 200, message = "El nombre del cliente no puede exceder 200 caracteres")
    private String nombreCliente;
    
    @Size(max = 50, message = "El documento/NIT no puede exceder 50 caracteres")
    private String documentoNit;
    
    // Información de la devolución
    @Size(max = 1000, message = "El motivo no puede exceder 1000 caracteres")
    private String motivo;
    
    @Size(max = 2000, message = "Las observaciones generales no pueden exceder 2000 caracteres")
    private String observacionesGenerales;
    
    @NotEmpty(message = "Debe incluir al menos un item")
    @Valid
    private List<ItemGarantiaRequestDTO> items;
}

