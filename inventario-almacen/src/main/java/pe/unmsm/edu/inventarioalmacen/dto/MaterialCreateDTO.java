package pe.unmsm.edu.inventarioalmacen.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialCreateDTO {
    
    @NotBlank(message = "El código es requerido")
    @Size(max = 50, message = "El código no puede exceder 50 caracteres")
    private String codigo;
    
    @NotBlank(message = "El nombre es requerido")
    @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
    private String nombre;
    
    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String descripcion;
    
    @NotNull(message = "La categoría es requerida")
    private UUID categoriaId;
    
    @NotNull(message = "La unidad base es requerida")
    private UUID unidadBaseId;
    
    private Boolean activo;
    
    private ReordenConfigDTO reordenConfig;
    
    // Stock inicial al crear o ajuste de stock al editar
    private Double stockInicial;
}
