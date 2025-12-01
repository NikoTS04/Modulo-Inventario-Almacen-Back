package pe.unmsm.edu.inventarioalmacen.factory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pe.unmsm.edu.inventarioalmacen.dto.MaterialCreateDTO;
import pe.unmsm.edu.inventarioalmacen.entity.*;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class MaterialFactory {

    public Material crearMaterial(String codigo, String nombre, String descripcion,
                                  CategoriaMaterial categoria, UnidadMedida unidadBase) {
        log.debug("Creando material con código: {}", codigo);
        
        return Material.builder()
                .codigo(codigo)
                .nombre(nombre)
                .descripcion(descripcion)
                .categoria(categoria)
                .unidadBase(unidadBase)
                .activo(true)
                .build();
    }

    public Material reconstruirDesdeDTO(MaterialCreateDTO dto, CategoriaMaterial categoria, 
                                       UnidadMedida unidadBase) {
        log.debug("Reconstruyendo material desde DTO: {}", dto.getCodigo());
        
        Material material = Material.builder()
                .codigo(dto.getCodigo())
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .categoria(categoria)
                .unidadBase(unidadBase)
                .activo(dto.getActivo() != null ? dto.getActivo() : true)
                .build();

        // Crear configuración de reorden si está presente
        if (dto.getReordenConfig() != null) {
            ReordenConfig config = ReordenConfig.builder()
                    .material(material)
                    .stockMinimo(dto.getReordenConfig().getStockMinimo())
                    .puntoReorden(dto.getReordenConfig().getPuntoReorden())
                    .activarAlerta(dto.getReordenConfig().getActivarAlerta() != null ? 
                            dto.getReordenConfig().getActivarAlerta() : true)
                    .build();
            material.setReordenConfig(config);
        }

        // Crear inventario inicial
        Inventario inventario = Inventario.builder()
                .material(material)
                .cantidadDisponible(BigDecimal.ZERO)
                .cantidadComprometida(BigDecimal.ZERO)
                .build();
        material.setInventario(inventario);

        return material;
    }
}
