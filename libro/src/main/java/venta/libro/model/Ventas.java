package venta.libro.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ventas")
public class Ventas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotNull(message = "El subtotal es obligatorio")
    @PositiveOrZero(message = "El subtotal no puede ser negativo")
    private Double subtotal;

    @Column(nullable = false)
    private Double descuentoTotal;

    @Column(nullable = false)
    @NotNull(message = "El total es obligatorio")
    @PositiveOrZero(message = "El total no puede ser negativo")
    private Double total;

    @ManyToOne
    @JoinColumn(name = "id_medio_pago", nullable = true)
    private MedioPago medioPago;

    @ManyToOne
    @JoinColumn(name = "id_estado_venta", nullable = false)
    @NotNull(message = "El estado de la venta es obligatorio")
    private EstadoVenta estadoVenta;
  
    @Column(nullable = false)
    private Double vuelto;

    @Column(nullable = false)
    @NotNull(message = "El ID del cliente es obligatorio")
    @Positive(message = "El ID del cliente debe ser un número positivo")
    private Long idCliente;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @NotEmpty(message = "La venta debe contener al menos una orden de venta (producto)")
    private List<OrdenVenta> ordenventas;
}

