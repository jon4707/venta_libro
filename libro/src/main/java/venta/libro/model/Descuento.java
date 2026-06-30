package venta.libro.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "descuentos")
public class Descuento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "fecha_creacion")
    private Date fechaCreacion;

    @Column(name = "fecha_vencimiento", nullable = false)
    private Date fechaVencimiento;

    @Column(nullable = false)
    private Integer porcentaje;

    @Column(nullable = false)
    private Boolean estado = true;
}
