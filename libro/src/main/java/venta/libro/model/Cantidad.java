package venta.libro.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cantidades")
public class Cantidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_libro", nullable = false)
    private Long idLibro;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "id_venta", nullable = false, insertable = false, updatable = false)
    private Long idVenta;

    @ManyToOne
    @JoinColumn(name = "id_venta", nullable = false)
    @JsonIgnore
    private Ventas venta;
}
