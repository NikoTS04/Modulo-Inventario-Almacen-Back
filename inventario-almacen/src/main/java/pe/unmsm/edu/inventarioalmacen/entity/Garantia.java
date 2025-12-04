package pe.unmsm.edu.inventarioalmacen.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import pe.unmsm.edu.inventarioalmacen.entity.enums.DestinoGarantia;
import pe.unmsm.edu.inventarioalmacen.entity.enums.EstadoGarantia;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "garantias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Garantia {

    @Id
    @UuidGenerator
    @Column(name = "garantia_id", columnDefinition = "BINARY(16)")
    private UUID garantiaId;

    @Column(name = "codigo", nullable = false, length = 50, unique = true)
    private String codigo;

    @Column(name = "id_devolucion", columnDefinition = "BINARY(16)")
    private UUID idDevolucion;

    @Column(name = "nombre_cliente", length = 200)
    private String nombreCliente;

    @Column(name = "documento_nit", length = 50)
    private String documentoNit;

    @Column(name = "motivo", columnDefinition = "TEXT")
    private String motivo;

    @Column(name = "observaciones_generales", columnDefinition = "TEXT")
    private String observacionesGenerales;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 30)
    @Builder.Default
    private EstadoGarantia estado = EstadoGarantia.RECIBIDO;

    @Enumerated(EnumType.STRING)
    @Column(name = "destino", length = 30)
    private DestinoGarantia destino;

    @Column(name = "inspector_id", columnDefinition = "BINARY(16)")
    private UUID inspectorId;

    @Column(name = "observaciones_inspeccion", columnDefinition = "TEXT")
    private String observacionesInspeccion;

    @Column(name = "fecha_inspeccion")
    private LocalDateTime fechaInspeccion;

    @Column(name = "usuario_responsable_id", columnDefinition = "BINARY(16)")
    private UUID usuarioResponsableId;

    @Column(name = "comentario_decision", columnDefinition = "TEXT")
    private String comentarioDecision;

    @Column(name = "fecha_decision")
    private LocalDateTime fechaDecision;

    @OneToMany(mappedBy = "garantia", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ItemGarantia> items = new ArrayList<>();

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion", nullable = false)
    private LocalDateTime fechaModificacion;

    @Column(name = "usuario_creacion", length = 100)
    private String usuarioCreacion;

    @Column(name = "usuario_modificacion", length = 100)
    private String usuarioModificacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaModificacion = LocalDateTime.now();
        if (codigo == null) {
            codigo = generarCodigo();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaModificacion = LocalDateTime.now();
    }

    private String generarCodigo() {
        return "GAR-" + System.currentTimeMillis();
    }

    public void agregarItem(ItemGarantia item) {
        items.add(item);
        item.setGarantia(this);
    }

    public void registrarInspeccion(UUID inspectorId, String observaciones) {
        this.inspectorId = inspectorId;
        this.observacionesInspeccion = observaciones;
        this.fechaInspeccion = LocalDateTime.now();
        this.estado = EstadoGarantia.PENDIENTE_DECISION;
    }

    public void confirmarDecision(DestinoGarantia destino, UUID usuarioResponsableId, String comentario) {
        this.destino = destino;
        this.usuarioResponsableId = usuarioResponsableId;
        this.comentarioDecision = comentario;
        this.fechaDecision = LocalDateTime.now();
        
        switch (destino) {
            case REPARACION:
                this.estado = EstadoGarantia.EN_REPARACION;
                break;
            case REINTEGRO:
            case ELIMINACION:
                this.estado = EstadoGarantia.COMPLETADO;
                break;
        }
    }

    public void iniciarRevision() {
        this.estado = EstadoGarantia.EN_REVISION;
    }

    public void completar() {
        this.estado = EstadoGarantia.COMPLETADO;
    }

    public void cancelar() {
        this.estado = EstadoGarantia.CANCELADO;
    }
}

