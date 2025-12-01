package pe.unmsm.edu.inventarioalmacen.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reorden_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReordenConfig {

    @Id
    @UuidGenerator
    @Column(name = "config_id", columnDefinition = "BINARY(16)")
    private UUID configId;

    @OneToOne
    @JoinColumn(name = "material_id", nullable = false, unique = true)
    private Material material;

    @Column(name = "stock_minimo", nullable = false, precision = 18, scale = 4)
    private BigDecimal stockMinimo;

    @Column(name = "punto_reorden", nullable = false, precision = 18, scale = 4)
    private BigDecimal puntoReorden;

    @Column(name = "activar_alerta", nullable = false)
    @Builder.Default
    private boolean activarAlerta = true;

    @Column(name = "ultima_alerta")
    private LocalDateTime ultimaAlerta;

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

    public void actualizarConfig(BigDecimal minimo, BigDecimal punto, boolean activarAlerta) {
        this.stockMinimo = minimo;
        this.puntoReorden = punto;
        this.activarAlerta = activarAlerta;
    }

    public boolean debeAlerta(BigDecimal stockActual) {
        if (stockActual == null) {
            return false;
        }
        return activarAlerta && stockActual.compareTo(puntoReorden) <= 0;
    }

    public void registrarAlertaEnviada() {
        this.ultimaAlerta = LocalDateTime.now();
    }
}
