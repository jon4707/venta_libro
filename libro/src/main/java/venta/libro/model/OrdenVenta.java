package venta.libro.model;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orden")
public class OrdenVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  
    
    @Column(nullable = false)
    private Long idUsuario;

    @Column (nullable = false)
    @JsonFormat
    private Date fechaOrden;

    @Column (nullable = false)
    private Double subtotal;

    @Column (nullable = false)
    private Double impuestos;

    @Column (nullable = false)
    private Double total;

    @Column (nullable = false)
    private String direccionEnvio;

    @Column (nullable = false)
    private String direccionFacturacion;

    @Enumerated(EnumType.STRING)
    private EstadoOrden estadoOrden;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta", nullable = false)
    @JsonBackReference
    @ToString.Exclude   
    private Ventas venta;
}
