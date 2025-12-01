package pe.unmsm.edu.inventarioalmacen.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import pe.unmsm.edu.inventarioalmacen.dto.MaterialDetailDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "materiales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Material {

    @Id
    @UuidGenerator
    @Column(name = "material_id", columnDefinition = "BINARY(16)")
    private UUID materialId;

    @Column(name = "codigo", nullable = false, length = 50, unique = true)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoriaMaterial categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidad_base_id", nullable = false)
    private UnidadMedida unidadBase;

    @Column(name = "activo", nullable = false)
    @Builder.Default
    private boolean activo = true;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion", nullable = false)
    private LocalDateTime fechaModificacion;

    @Column(name = "usuario_creacion", length = 100)
    private String usuarioCreacion;

    @Column(name = "usuario_modificacion", length = 100)
    private String usuarioModificacion;

    @OneToOne(mappedBy = "material", cascade = CascadeType.ALL, orphanRemoval = true)
    private ReordenConfig reordenConfig;

    @OneToOne(mappedBy = "material", cascade = CascadeType.ALL, orphanRemoval = true)
    private Inventario inventario;

    @OneToMany(mappedBy = "material", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UnidadAlternativa> unidadesAlternativas = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaModificacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaModificacion = LocalDateTime.now();
    }

    public void definirReordenConfig(BigDecimal minimo, BigDecimal puntoReorden, boolean activarAlerta) {
        if (this.reordenConfig == null) {
            this.reordenConfig = new ReordenConfig();
            this.reordenConfig.setMaterial(this);
        }
        this.reordenConfig.actualizarConfig(minimo, puntoReorden, activarAlerta);
    }

    public void activar() {
        this.activo = true;
    }

    public void desactivar() {
        this.activo = false;
    }

    public void actualizarDatos(String nombre, String descripcion, CategoriaMaterial categoria, UnidadMedida unidadBase) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.unidadBase = unidadBase;
    }

    public void asignarUnidadAlternativa(UnidadMedida unidad, BigDecimal factor) {
        UnidadAlternativa unidadAlt = UnidadAlternativa.builder()
                .material(this)
                .unidad(unidad)
                .factorConversion(factor)
                .activo(true)
                .build();
        this.unidadesAlternativas.add(unidadAlt);
    }

    public boolean verificarAlertaReorden(BigDecimal stockActual) {
        if (this.reordenConfig == null) {
            return false;
        }
        return this.reordenConfig.debeAlerta(stockActual);
    }

    public MaterialDetailDTO toDTO() {
        return MaterialDetailDTO.builder()
                .materialId(this.materialId)
                .codigo(this.codigo)
                .nombre(this.nombre)
                .descripcion(this.descripcion)
                .activo(this.activo)
                .fechaCreacion(this.fechaCreacion)
                .fechaModificacion(this.fechaModificacion)
                .usuarioCreacion(this.usuarioCreacion)
                .usuarioModificacion(this.usuarioModificacion)
                .build();
    }
}
