package pe.unmsm.edu.inventarioalmacen.dto;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialListResponseDTO {
    private List<MaterialSummaryDTO> items;
    private int page;
    private int limit;
    private long total;
    private int totalPages;
}
