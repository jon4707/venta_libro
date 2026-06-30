package venta.libro.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import venta.libro.dto.AgregarProductoRequest;
import venta.libro.dto.CantidadDTO;
import venta.libro.dto.CompraOnlineRequest;
import venta.libro.dto.CrearVentaRequest;
import venta.libro.dto.FinalizarCompraRequest;
import venta.libro.dto.VentasDTO;
import venta.libro.model.Cantidad;
import venta.libro.model.Descuento;
import venta.libro.model.MedioPago;
import venta.libro.model.TipoVenta;
import venta.libro.model.Ventas;
import venta.libro.repository.CantidadRepository;
import venta.libro.repository.DescuentoRepository;
import venta.libro.repository.VentasRepository;

@ExtendWith(MockitoExtension.class)
class VentasServiceTest {

    @Mock
    private VentasRepository ventasRepository;

    @Mock
    private CantidadRepository cantidadRepository;

    @Mock
    private DescuentoRepository descuentoRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private VentasService ventasService;

    @Test
    void testCrearVentaCajero() {
        CrearVentaRequest request = new CrearVentaRequest(1L, 1L);

        Ventas savedVenta = new Ventas();
        savedVenta.setId(1L);
        savedVenta.setIdSucursal(1L);
        savedVenta.setFechaCompra(new Date(Instant.now().toEpochMilli()));
        savedVenta.setSubtotal(0.0);
        savedVenta.setDescuentoTotal(0.0);
        savedVenta.setImpuestos(0.0);
        savedVenta.setTotal(0.0);
        savedVenta.setTipoVenta(TipoVenta.VENTA_CAJERO);
        savedVenta.setIdCliente(1L);

        when(ventasRepository.save(any(Ventas.class))).thenReturn(savedVenta);

        VentasDTO result = ventasService.crearVentaCajero(request);

        assertThat(result.getIdSucursal()).isEqualTo(1L);
        assertThat(result.getTipoVenta()).isEqualTo(TipoVenta.VENTA_CAJERO);
        assertThat(result.getIdCliente()).isEqualTo(1L);
        assertThat(result.getSubtotal()).isEqualTo(0.0);
    }

    @Test
    void testAñadirProducto_Success() {
        Long idVenta = 1L;
        Ventas venta = new Ventas();
        venta.setId(idVenta);
        venta.setIdSucursal(1L);
        venta.setSubtotal(0.0);
        venta.setProductos(new ArrayList<>());

        AgregarProductoRequest request = new AgregarProductoRequest(10L, 2, null);

        when(ventasRepository.findById(idVenta)).thenReturn(Optional.of(venta));
        when(restTemplate.getForObject(anyString(), eq(Double.class), anyLong())).thenReturn(9900.0);
        when(restTemplate.postForObject(contains("/stock-libros/validar"), any(),
                eq(Boolean.class))).thenReturn(true);
        when(cantidadRepository.save(any(Cantidad.class))).thenAnswer(invocation -> {
            Cantidad c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });

        CantidadDTO result = ventasService.añadirProducto(idVenta, request);

        assertThat(result.getIdLibro()).isEqualTo(10L);
        assertThat(result.getCantidad()).isEqualTo(2);
        assertThat(venta.getSubtotal()).isEqualTo(19800.0);
        verify(ventasRepository).save(venta);
    }

    @Test
    void testAñadirProducto_WithDiscount() {
        Long idVenta = 1L;
        Ventas venta = new Ventas();
        venta.setId(idVenta);
        venta.setIdSucursal(1L);
        venta.setSubtotal(0.0);
        venta.setDescuentoTotal(0.0);
        venta.setProductos(new ArrayList<>());

        AgregarProductoRequest request = new AgregarProductoRequest(10L, 2, 5L);

        Descuento descuento = new Descuento();
        descuento.setId(5L);
        descuento.setPorcentaje(10);
        descuento.setEstado(true);

        when(ventasRepository.findById(idVenta)).thenReturn(Optional.of(venta));
        when(restTemplate.getForObject(anyString(), eq(Double.class), anyLong())).thenReturn(9900.0);
        when(restTemplate.postForObject(contains("/stock-libros/validar"), any(),
                eq(Boolean.class))).thenReturn(true);
        when(descuentoRepository.findByIdAndEstadoTrue(5L)).thenReturn(Optional.of(descuento));
        when(cantidadRepository.save(any(Cantidad.class))).thenAnswer(invocation -> {
            Cantidad c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });

        CantidadDTO result = ventasService.añadirProducto(idVenta, request);

        assertThat(venta.getSubtotal()).isEqualTo(19800.0);
        assertThat(venta.getDescuentoTotal()).isCloseTo(990.0, within(0.001));
        assertThat(descuento.getEstado()).isFalse();
        verify(descuentoRepository).save(descuento);
    }

    @Test
    void testAñadirProducto_NotFound() {
        when(ventasRepository.findById(99L)).thenReturn(Optional.empty());

        AgregarProductoRequest request = new AgregarProductoRequest(10L, 1, null);

        assertThatThrownBy(() -> ventasService.añadirProducto(99L, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Venta no encontrada");
    }

    @Test
    void testEliminarProducto_Success() {
        Long idVenta = 1L;
        Long idLibro = 10L;
        Ventas venta = new Ventas();
        venta.setId(idVenta);
        venta.setSubtotal(19800.0);
        Cantidad c = new Cantidad();
        c.setId(1L);
        c.setIdLibro(idLibro);
        c.setCantidad(2);
        c.setVenta(venta);
        venta.setProductos(new ArrayList<>(List.of(c)));

        when(ventasRepository.findById(idVenta)).thenReturn(Optional.of(venta));
        when(cantidadRepository.findByIdVentaAndIdLibro(idVenta, idLibro)).thenReturn(Optional.of(c));
        when(restTemplate.getForObject(anyString(), eq(Double.class), anyLong())).thenReturn(9900.0);

        ventasService.eliminarProducto(idVenta, idLibro);

        assertThat(venta.getSubtotal()).isEqualTo(0.0);
        verify(cantidadRepository).delete(c);
        verify(ventasRepository).save(venta);
    }

    @Test
    void testEliminarProducto_NotFound() {
        Long idVenta = 1L;
        Ventas venta = new Ventas();
        venta.setId(idVenta);

        when(ventasRepository.findById(idVenta)).thenReturn(Optional.of(venta));
        when(cantidadRepository.findByIdVentaAndIdLibro(idVenta, 99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ventasService.eliminarProducto(idVenta, 99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Producto no encontrado en la venta");
    }

    @Test
    void testFinalizarCompra_Efectivo_Success() {
        Long idVenta = 1L;
        Ventas venta = new Ventas();
        venta.setId(idVenta);
        venta.setSubtotal(100000.0);
        venta.setDescuentoTotal(0.0);
        Cantidad c = new Cantidad();
        c.setIdLibro(10L);
        c.setCantidad(2);
        c.setVenta(venta);
        venta.setProductos(new ArrayList<>(List.of(c)));

        FinalizarCompraRequest request = new FinalizarCompraRequest(MedioPago.EFECTIVO, 150000.0);

        when(ventasRepository.findById(idVenta)).thenReturn(Optional.of(venta));
        when(restTemplate.postForObject(contains("/stock-libros/reducir"), any(),
                eq(String.class))).thenReturn("ok");
        when(restTemplate.postForObject(contains("/unidades-vendidas"), isNull(),
                eq(String.class), anyLong(), anyInt())).thenReturn("ok");
        when(ventasRepository.save(any(Ventas.class))).thenReturn(venta);

        VentasDTO result = ventasService.finalizarCompra(idVenta, request);

        assertThat(result.getImpuestos()).isCloseTo(19000.0, within(0.001));
        assertThat(result.getTotal()).isCloseTo(119000.0, within(0.001));
        assertThat(result.getMontoPagado()).isCloseTo(150000.0, within(0.001));
        assertThat(result.getVuelto()).isCloseTo(31000.0, within(0.001));
        assertThat(result.getMedioPago()).isEqualTo(MedioPago.EFECTIVO);
    }

    @Test
    void testFinalizarCompra_Efectivo_MontoInsuficiente() {
        Long idVenta = 1L;
        Ventas venta = new Ventas();
        venta.setId(idVenta);
        venta.setSubtotal(100000.0);
        venta.setDescuentoTotal(0.0);
        Cantidad c = new Cantidad();
        c.setIdLibro(10L);
        c.setCantidad(2);
        c.setVenta(venta);
        venta.setProductos(new ArrayList<>(List.of(c)));

        FinalizarCompraRequest request = new FinalizarCompraRequest(MedioPago.EFECTIVO, 100000.0);

        when(ventasRepository.findById(idVenta)).thenReturn(Optional.of(venta));

        assertThatThrownBy(() -> ventasService.finalizarCompra(idVenta, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Monto pagado insuficiente. Total: 119000.0");
    }

    @Test
    void testFinalizarCompra_Tarjeta() {
        Long idVenta = 1L;
        Ventas venta = new Ventas();
        venta.setId(idVenta);
        venta.setSubtotal(100000.0);
        venta.setDescuentoTotal(0.0);
        Cantidad c = new Cantidad();
        c.setIdLibro(10L);
        c.setCantidad(2);
        c.setVenta(venta);
        venta.setProductos(new ArrayList<>(List.of(c)));

        FinalizarCompraRequest request = new FinalizarCompraRequest(MedioPago.CREDITO, null);

        when(ventasRepository.findById(idVenta)).thenReturn(Optional.of(venta));
        when(restTemplate.postForObject(contains("/stock-libros/reducir"), any(),
                eq(String.class))).thenReturn("ok");
        when(restTemplate.postForObject(contains("/unidades-vendidas"), isNull(),
                eq(String.class), anyLong(), anyInt())).thenReturn("ok");
        when(ventasRepository.save(any(Ventas.class))).thenReturn(venta);

        VentasDTO result = ventasService.finalizarCompra(idVenta, request);

        assertThat(result.getImpuestos()).isCloseTo(19000.0, within(0.001));
        assertThat(result.getTotal()).isCloseTo(119000.0, within(0.001));
        assertThat(result.getMontoPagado()).isCloseTo(119000.0, within(0.001));
        assertThat(result.getVuelto()).isEqualTo(0.0);
        assertThat(result.getMedioPago()).isEqualTo(MedioPago.CREDITO);
    }

    @Test
    void testFinalizarCompra_SinProductos() {
        Long idVenta = 1L;
        Ventas venta = new Ventas();
        venta.setId(idVenta);
        venta.setProductos(new ArrayList<>());

        FinalizarCompraRequest request = new FinalizarCompraRequest(MedioPago.EFECTIVO, 1000.0);

        when(ventasRepository.findById(idVenta)).thenReturn(Optional.of(venta));

        assertThatThrownBy(() -> ventasService.finalizarCompra(idVenta, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("La venta no tiene productos");
    }

    @Test
    void testRealizarCompraOnline_WithDiscount() {
        CompraOnlineRequest request = new CompraOnlineRequest();
        request.setIdOrden(1L);
        request.setIdCliente(1L);
        request.setIdSucursal(1L);
        request.setDescuentoId(5L);
        request.setMedioPago(MedioPago.CREDITO);

        CompraOnlineRequest.ItemCarritoDTO item = new CompraOnlineRequest.ItemCarritoDTO();
        item.setIdLibro(10L);
        item.setCantidad(2);
        request.setItems(List.of(item));

        Descuento descuento = new Descuento();
        descuento.setId(5L);
        descuento.setPorcentaje(10);
        descuento.setEstado(true);

        when(restTemplate.getForObject(anyString(), eq(Double.class), anyLong())).thenReturn(9900.0);
        when(restTemplate.postForObject(contains("/stock-libros/validar"), any(),
                eq(Boolean.class))).thenReturn(true);
        when(descuentoRepository.findByIdAndEstadoTrue(5L)).thenReturn(Optional.of(descuento));
        when(restTemplate.postForObject(contains("/stock-libros/reducir"), any(),
                eq(String.class))).thenReturn("ok");
        when(restTemplate.postForObject(contains("/unidades-vendidas"), isNull(),
                eq(String.class), anyLong(), anyInt())).thenReturn("ok");
        when(ventasRepository.save(any(Ventas.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VentasDTO result = ventasService.realizarCompraOnline(request);

        assertThat(result.getSubtotal()).isCloseTo(19800.0, within(0.001));
        assertThat(result.getDescuentoTotal()).isCloseTo(990.0, within(0.001));
        assertThat(result.getImpuestos()).isCloseTo(3573.9, within(0.001));
        assertThat(result.getTotal()).isCloseTo(22383.9, within(0.001));
        assertThat(result.getMontoPagado()).isCloseTo(22383.9, within(0.001));
        assertThat(result.getVuelto()).isEqualTo(0.0);
        assertThat(result.getTipoVenta()).isEqualTo(TipoVenta.VENTA_WEB);
        assertThat(descuento.getEstado()).isFalse();
    }

    @Test
    void testRealizarCompraOnline_StockFails() {
        CompraOnlineRequest request = new CompraOnlineRequest();
        request.setIdOrden(1L);
        request.setIdCliente(1L);
        request.setIdSucursal(1L);
        request.setMedioPago(MedioPago.CREDITO);

        CompraOnlineRequest.ItemCarritoDTO item = new CompraOnlineRequest.ItemCarritoDTO();
        item.setIdLibro(10L);
        item.setCantidad(2);
        request.setItems(List.of(item));

        when(restTemplate.getForObject(anyString(), eq(Double.class), anyLong())).thenReturn(9900.0);
        when(restTemplate.postForObject(contains("/stock-libros/validar"), any(),
                eq(Boolean.class))).thenReturn(true);
        when(restTemplate.postForObject(contains("/stock-libros/reducir"), any(),
                eq(String.class))).thenThrow(new RuntimeException("Stock error"));

        assertThatThrownBy(() -> ventasService.realizarCompraOnline(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Pago rechazado: Error al reducir stock del libro 10");
    }

    @Test
    void testListarVentas() {
        Ventas v1 = new Ventas();
        v1.setId(1L);
        Ventas v2 = new Ventas();
        v2.setId(2L);

        when(ventasRepository.findAll()).thenReturn(List.of(v1, v2));

        List<VentasDTO> result = ventasService.listarVentas();

        assertThat(result).hasSize(2);
    }

    @Test
    void testObtenerVenta_Found() {
        Ventas venta = new Ventas();
        venta.setId(1L);

        when(ventasRepository.findById(1L)).thenReturn(Optional.of(venta));

        VentasDTO result = ventasService.obtenerVenta(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void testObtenerVenta_NotFound() {
        when(ventasRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ventasService.obtenerVenta(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Venta no encontrada");
    }
}
