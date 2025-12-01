package pe.unmsm.edu.inventarioalmacen.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inventarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventario {

    @Id
    @UuidGenerator
    @Column(name = "inventario_id", columnDefinition = "BINARY(16)")
    private UUID inventarioId;

    @OneToOne
    @JoinColumn(name = "material_id", nullable = false, unique = true)
    private Material material;

    @Column(name = "cantidad_disponible", nullable = false, precision = 18, scale = 4)
    @Builder.Default
    private BigDecimal cantidadDisponible = BigDecimal.ZERO;

    @Column(name = "cantidad_comprometida", nullable = false, precision = 18, scale = 4)
    @Builder.Default
    private BigDecimal cantidadComprometida = BigDecimal.ZERO;

    @Column(name = "cantidad_total", insertable = false, updatable = false, precision = 18, scale = 4)
    private BigDecimal cantidadTotal;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    public void actualizarStock(BigDecimal nuevaCantidad) {
        this.cantidadDisponible = nuevaCantidad;
    }

    public boolean reservarCantidad(BigDecimal cantidad) {
        if (this.cantidadDisponible.compareTo(cantidad) >= 0) {
            this.cantidadDisponible = this.cantidadDisponible.subtract(cantidad);
            this.cantidadComprometida = this.cantidadComprometida.add(cantidad);
            return true;
        }
        return false;
    }

    public void liberarReserva(BigDecimal cantidad) {
        this.cantidadComprometida = this.cantidadComprometida.subtract(cantidad);
        this.cantidadDisponible = this.cantidadDisponible.add(cantidad);
    }

    public boolean verificarReorden() {
        if (material.getReordenConfig() == null) {
            return false;
        }
        BigDecimal total = cantidadDisponible.add(cantidadComprometida);
        return material.verificarAlertaReorden(total);
    }
}
