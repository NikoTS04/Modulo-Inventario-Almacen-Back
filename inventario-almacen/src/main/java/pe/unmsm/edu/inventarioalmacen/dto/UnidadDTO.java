package pe.unmsm.edu.inventarioalmacen.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnidadDTO {
    private UUID unidadId;
    
    @NotBlank(message = "El nombre es requerido")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    private String nombre;
    
    @NotBlank(message = "El símbolo es requerido")
    @Size(max = 10, message = "El símbolo no puede exceder 10 caracteres")
    private String simbolo;
    
    @Size(max = 20, message = "El tipo no puede exceder 20 caracteres")
    private String tipo;
    
    private Boolean activo;
}
