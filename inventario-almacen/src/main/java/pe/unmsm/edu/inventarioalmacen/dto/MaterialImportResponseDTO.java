package pe.unmsm.edu.inventarioalmacen.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO para respuesta de importación de materiales
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialImportResponseDTO {
    
    private int totalProcesados;
    private int exitosos;
    private int fallidos;
    
    @Builder.Default
    private List<String> errores = new ArrayList<>();
    
    @Builder.Default
    private List<MaterialDetailDTO> materialesCreados = new ArrayList<>();
    
    public void agregarError(int linea, String mensaje) {
        errores.add(String.format("Línea %d: %s", linea, mensaje));
    }
}
