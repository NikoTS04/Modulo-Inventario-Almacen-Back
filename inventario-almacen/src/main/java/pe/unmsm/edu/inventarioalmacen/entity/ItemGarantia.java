package pe.unmsm.edu.inventarioalmacen.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import pe.unmsm.edu.inventarioalmacen.entity.enums.EstadoFisico;
import pe.unmsm.edu.inventarioalmacen.entity.enums.ResultadoInspeccion;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "items_garantia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemGarantia {

    @Id
    @UuidGenerator
    @Column(name = "item_garantia_id", columnDefinition = "BINARY(16)")
    private UUID itemGarantiaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "garantia_id", nullable = false)
    private Garantia garantia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @Column(name = "cantidad", nullable = false, precision = 15, scale = 4)
    private BigDecimal cantidad;

    @Column(name = "lote", length = 100)
    private String lote;

    @Column(name = "motivo_devolucion", length = 500)
    private String motivoDevolucion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_fisico", length = 30)
    private EstadoFisico estadoFisico;

    @Enumerated(EnumType.STRING)
    @Column(name = "resultado_inspeccion", length = 30)
    private ResultadoInspeccion resultadoInspeccion;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion", nullable = false)
    private LocalDateTime fechaModificacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaModificacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaModificacion = LocalDateTime.now();
    }

    public void registrarResultadoInspeccion(EstadoFisico estadoFisico, ResultadoInspeccion resultado) {
        this.estadoFisico = estadoFisico;
        this.resultadoInspeccion = resultado;
    }
}

