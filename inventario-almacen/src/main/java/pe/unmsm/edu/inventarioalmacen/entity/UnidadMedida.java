package pe.unmsm.edu.inventarioalmacen.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "unidades_medida")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnidadMedida {

    @Id
    @UuidGenerator
    @Column(name = "unidad_id", columnDefinition = "BINARY(16)")
    private UUID unidadId;

    @Column(name = "nombre", nullable = false, length = 50, unique = true)
    private String nombre;

    @Column(name = "simbolo", nullable = false, length = 10, unique = true)
    private String simbolo;

    @Column(name = "tipo", length = 20)
    private String tipo; // PESO, VOLUMEN, LONGITUD, UNIDAD

    @Column(name = "activo", nullable = false)
    @Builder.Default
    private boolean activo = true;

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

    public BigDecimal convertir(BigDecimal valor, UnidadMedida otraUnidad) {
        // Implementación básica - puede ser mejorada con lógica de conversión
        if (this.equals(otraUnidad)) {
            return valor;
        }
        // Por ahora retorna el mismo valor, la conversión real se hace en la base de datos
        return valor;
    }
}
