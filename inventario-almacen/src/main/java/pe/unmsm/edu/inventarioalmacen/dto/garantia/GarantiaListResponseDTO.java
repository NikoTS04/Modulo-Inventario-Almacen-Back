package pe.unmsm.edu.inventarioalmacen.dto.garantia;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GarantiaListResponseDTO {
    
    private List<GarantiaDTO> items;
    private int page;
    private int limit;
    private long total;
    private int totalPages;
}

