package pe.unmsm.edu.inventarioalmacen.mapper;

import org.springframework.stereotype.Component;
import pe.unmsm.edu.inventarioalmacen.dto.*;
import pe.unmsm.edu.inventarioalmacen.entity.*;

@Component
public class MaterialMapper {

    public MaterialDetailDTO toDetailDTO(Material material) {
        MaterialDetailDTO.MaterialDetailDTOBuilder builder = MaterialDetailDTO.builder()
                .materialId(material.getMaterialId())
                .codigo(material.getCodigo())
                .nombre(material.getNombre())
                .descripcion(material.getDescripcion())
                .activo(material.isActivo())
                .fechaCreacion(material.getFechaCreacion())
                .fechaModificacion(material.getFechaModificacion())
                .usuarioCreacion(material.getUsuarioCreacion())
                .usuarioModificacion(material.getUsuarioModificacion());

        if (material.getCategoria() != null) {
            builder.categoriaId(material.getCategoria().getCategoriaId())
                   .categoriaNombre(material.getCategoria().getNombre());
        }

        if (material.getUnidadBase() != null) {
            builder.unidadBaseId(material.getUnidadBase().getUnidadId())
                   .unidadBaseNombre(material.getUnidadBase().getNombre())
                   .unidadBaseSimbolo(material.getUnidadBase().getSimbolo());
        }

        if (material.getInventario() != null) {
            Inventario inv = material.getInventario();
            builder.stockDisponible(inv.getCantidadDisponible())
                   .stockComprometido(inv.getCantidadComprometida())
                   .stockTotal(inv.getCantidadTotal());
        }

        if (material.getReordenConfig() != null) {
            ReordenConfig config = material.getReordenConfig();
            builder.reordenConfig(ReordenConfigDTO.builder()
                    .stockMinimo(config.getStockMinimo())
                    .puntoReorden(config.getPuntoReorden())
                    .activarAlerta(config.isActivarAlerta())
                    .build());
            
            if (material.getInventario() != null) {
                builder.alertaReorden(material.verificarAlertaReorden(material.getInventario().getCantidadTotal()));
            }
        }

        return builder.build();
    }

    public MaterialSummaryDTO toSummaryDTO(Material material) {
        MaterialSummaryDTO.MaterialSummaryDTOBuilder builder = MaterialSummaryDTO.builder()
                .materialId(material.getMaterialId())
                .codigo(material.getCodigo())
                .nombre(material.getNombre())
                .activo(material.isActivo());

        if (material.getCategoria() != null) {
            builder.categoriaNombre(material.getCategoria().getNombre());
        }

        if (material.getUnidadBase() != null) {
            builder.unidadBaseSimbolo(material.getUnidadBase().getSimbolo());
        }

        if (material.getInventario() != null) {
            builder.stockTotal(material.getInventario().getCantidadTotal());
            
            if (material.getReordenConfig() != null) {
                builder.alertaReorden(material.verificarAlertaReorden(material.getInventario().getCantidadTotal()));
            }
        }

        return builder.build();
    }

    public CategoriaDTO toCategoriaDTO(CategoriaMaterial categoria) {
        return CategoriaDTO.builder()
                .categoriaId(categoria.getCategoriaId())
                .nombre(categoria.getNombre())
                .descripcion(categoria.getDescripcion())
                .activo(categoria.isActivo())
                .build();
    }

    public UnidadDTO toUnidadDTO(UnidadMedida unidad) {
        return UnidadDTO.builder()
                .unidadId(unidad.getUnidadId())
                .nombre(unidad.getNombre())
                .simbolo(unidad.getSimbolo())
                .tipo(unidad.getTipo())
                .activo(unidad.isActivo())
                .build();
    }
}
