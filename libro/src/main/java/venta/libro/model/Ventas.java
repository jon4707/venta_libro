package venta.libro.model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Ventas")
public class Ventas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_sucursal")
    private Long idSucursal;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cantidad> productos = new ArrayList<>();

    @Column(name = "fecha_compra", nullable = false)
    private Date fechaCompra;

    @Column(name = "subtotal", nullable = false)
    private Double subtotal = 0.0;

    @Column(name = "descuento_total")
    private Double descuentoTotal = 0.0;

    @Column(name = "impuestos")
    private Double impuestos = 0.0;

    @Column(name = "total", nullable = false)
    private Double total = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(name = "medio_pago")
    private MedioPago medioPago;

    @Column(name = "monto_pagado")
    private Double montoPagado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_venta", nullable = false)
    private TipoVenta tipoVenta;

    @Column(name = "vuelto")
    private Double vuelto;

    @Column(name = "id_cliente")
    private Long idCliente;
}
