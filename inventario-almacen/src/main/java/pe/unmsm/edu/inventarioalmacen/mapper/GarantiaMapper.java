package pe.unmsm.edu.inventarioalmacen.mapper;

import org.springframework.stereotype.Component;
import pe.unmsm.edu.inventarioalmacen.dto.garantia.DevolucionDTO;
import pe.unmsm.edu.inventarioalmacen.dto.garantia.GarantiaDTO;
import pe.unmsm.edu.inventarioalmacen.dto.garantia.ItemGarantiaDTO;
import pe.unmsm.edu.inventarioalmacen.entity.Garantia;
import pe.unmsm.edu.inventarioalmacen.entity.ItemGarantia;
import pe.unmsm.edu.inventarioalmacen.entity.enums.DestinoGarantia;
import pe.unmsm.edu.inventarioalmacen.entity.enums.EstadoGarantia;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GarantiaMapper {

    public GarantiaDTO toDTO(Garantia garantia) {
        GarantiaDTO.GarantiaDTOBuilder builder = GarantiaDTO.builder()
                .garantiaId(garantia.getGarantiaId())
                .codigo(garantia.getCodigo())
                .idDevolucion(garantia.getIdDevolucion())
                .nombreCliente(garantia.getNombreCliente())
                .documentoNit(garantia.getDocumentoNit())
                .motivo(garantia.getMotivo())
                .observacionesGenerales(garantia.getObservacionesGenerales())
                .estado(garantia.getEstado())
                .destino(garantia.getDestino())
                .inspectorId(garantia.getInspectorId())
                .observacionesInspeccion(garantia.getObservacionesInspeccion())
                .fechaInspeccion(garantia.getFechaInspeccion())
                .usuarioResponsableId(garantia.getUsuarioResponsableId())
                .comentarioDecision(garantia.getComentarioDecision())
                .fechaDecision(garantia.getFechaDecision())
                .fechaCreacion(garantia.getFechaCreacion())
                .fechaModificacion(garantia.getFechaModificacion())
                .usuarioCreacion(garantia.getUsuarioCreacion())
                .usuarioModificacion(garantia.getUsuarioModificacion());

        if (garantia.getItems() != null && !garantia.getItems().isEmpty()) {
            List<ItemGarantiaDTO> itemsDTO = garantia.getItems().stream()
                    .map(this::toItemDTO)
                    .collect(Collectors.toList());
            builder.items(itemsDTO)
                   .totalItems(itemsDTO.size());
        } else {
            builder.totalItems(0);
        }

        return builder.build();
    }

    public GarantiaDTO toSummaryDTO(Garantia garantia) {
        return GarantiaDTO.builder()
                .garantiaId(garantia.getGarantiaId())
                .codigo(garantia.getCodigo())
                .idDevolucion(garantia.getIdDevolucion())
                .nombreCliente(garantia.getNombreCliente())
                .documentoNit(garantia.getDocumentoNit())
                .motivo(garantia.getMotivo())
                .estado(garantia.getEstado())
                .destino(garantia.getDestino())
                .totalItems(garantia.getItems() != null ? garantia.getItems().size() : 0)
                .fechaCreacion(garantia.getFechaCreacion())
                .fechaModificacion(garantia.getFechaModificacion())
                .build();
    }

    public ItemGarantiaDTO toItemDTO(ItemGarantia item) {
        ItemGarantiaDTO.ItemGarantiaDTOBuilder builder = ItemGarantiaDTO.builder()
                .itemGarantiaId(item.getItemGarantiaId())
                .cantidad(item.getCantidad())
                .lote(item.getLote())
                .motivoDevolucion(item.getMotivoDevolucion())
                .estadoFisico(item.getEstadoFisico())
                .resultadoInspeccion(item.getResultadoInspeccion())
                .observaciones(item.getObservaciones())
                .fechaCreacion(item.getFechaCreacion());

        if (item.getMaterial() != null) {
            builder.materialId(item.getMaterial().getMaterialId())
                   .materialCodigo(item.getMaterial().getCodigo())
                   .materialNombre(item.getMaterial().getNombre());
        }

        return builder.build();
    }

    /**
     * Convertir Garantia a DevolucionDTO (formato legacy)
     */
    public DevolucionDTO toDevolucionDTO(Garantia garantia) {
        List<DevolucionDTO.ItemDevolucionDTO> itemsDTO = garantia.getItems() != null
                ? garantia.getItems().stream()
                        .map(this::toItemDevolucionDTO)
                        .collect(Collectors.toList())
                : List.of();

        return DevolucionDTO.builder()
                .devolucionId(garantia.getGarantiaId())
                .codigo(garantia.getCodigo())
                .fechaRegistro(garantia.getFechaCreacion())
                .fechaActualizacion(garantia.getFechaModificacion())
                .estado(garantia.getEstado())
                .clienteNombre(garantia.getNombreCliente())
                .clienteDocumento(garantia.getDocumentoNit())
                .motivoGeneral(garantia.getMotivo())
                .observaciones(garantia.getObservacionesGenerales())
                .items(itemsDTO)
                .usuarioRegistro(garantia.getUsuarioCreacion())
                .usuarioActualizacion(garantia.getUsuarioModificacion())
                .build();
    }

    private DevolucionDTO.ItemDevolucionDTO toItemDevolucionDTO(ItemGarantia item) {
        String estadoItem = determinarEstadoItem(item);
        String resultadoInspeccion = item.getResultadoInspeccion() != null
                ? item.getResultadoInspeccion().name()
                : null;

        return DevolucionDTO.ItemDevolucionDTO.builder()
                .itemId(item.getItemGarantiaId())
                .materialId(item.getMaterial() != null ? item.getMaterial().getMaterialId() : null)
                .materialCodigo(item.getMaterial() != null ? item.getMaterial().getCodigo() : null)
                .materialNombre(item.getMaterial() != null ? item.getMaterial().getNombre() : null)
                .cantidad(item.getCantidad())
                .motivo(item.getMotivoDevolucion())
                .observaciones(item.getObservaciones())
                .estado(estadoItem)
                .destino(item.getGarantia().getDestino())
                .fechaInspeccion(item.getGarantia().getFechaInspeccion())
                .resultadoInspeccion(resultadoInspeccion)
                .build();
    }

    private String determinarEstadoItem(ItemGarantia item) {
        if (item.getResultadoInspeccion() == null) {
            return "PENDIENTE";
        }

        DestinoGarantia destino = item.getGarantia().getDestino();
        if (destino == null) {
            return "INSPECCIONADO";
        }

        return switch (destino) {
            case REINTEGRO -> "REINTEGRADO";
            case REPARACION -> "EN_REPARACION";
            case ELIMINACION -> "ELIMINADO";
        };
    }
}

