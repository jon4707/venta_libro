package venta.libro.dto;

import java.sql.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import venta.libro.model.MedioPago;
import venta.libro.model.TipoVenta;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentasDTO {
    private Long id;
    private Long idSucursal;
    private List<CantidadDTO> productos;
    private Date fechaCompra;
    private Double subtotal;
    private Double descuentoTotal;
    private Double impuestos;
    private Double total;
    private MedioPago medioPago;
    private Double montoPagado;
    private TipoVenta tipoVenta;
    private Double vuelto;
    private Long idCliente;
}
