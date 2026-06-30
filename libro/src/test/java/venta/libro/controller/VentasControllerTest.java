package venta.libro.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import venta.libro.dto.AgregarProductoRequest;
import venta.libro.dto.CantidadDTO;
import venta.libro.dto.CompraOnlineRequest;
import venta.libro.dto.CrearVentaRequest;
import venta.libro.dto.FinalizarCompraRequest;
import venta.libro.dto.VentasDTO;
import venta.libro.model.MedioPago;
import venta.libro.model.TipoVenta;
import venta.libro.service.VentasService;

@WebMvcTest(VentasController.class)
class VentasControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private VentasService ventasService;

    @Test
    void testCrearVenta() throws Exception {
        CrearVentaRequest request = new CrearVentaRequest(1L, 1L);
        VentasDTO dto = new VentasDTO();
        dto.setId(1L);
        dto.setTipoVenta(TipoVenta.VENTA_CAJERO);

        when(ventasService.crearVentaCajero(any())).thenReturn(dto);

        mockMvc.perform(post("/api/v1/ventas/crear")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tipoVenta").value("VENTA_CAJERO"));
    }

    @Test
    void testAñadirProducto() throws Exception {
        AgregarProductoRequest request = new AgregarProductoRequest(10L, 2, null);
        CantidadDTO dto = new CantidadDTO(1L, 1L, 10L, 2);

        when(ventasService.añadirProducto(eq(1L), any())).thenReturn(dto);

        mockMvc.perform(post("/api/v1/ventas/1/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idLibro").value(10))
                .andExpect(jsonPath("$.cantidad").value(2));
    }

    @Test
    void testEliminarProducto() throws Exception {
        doNothing().when(ventasService).eliminarProducto(1L, 10L);

        mockMvc.perform(delete("/api/v1/ventas/1/productos/10"))
                .andExpect(status().isNoContent());

        verify(ventasService).eliminarProducto(1L, 10L);
    }

    @Test
    void testFinalizarCompra() throws Exception {
        FinalizarCompraRequest request = new FinalizarCompraRequest(MedioPago.CREDITO, null);
        VentasDTO dto = new VentasDTO();
        dto.setId(1L);
        dto.setMedioPago(MedioPago.CREDITO);

        when(ventasService.finalizarCompra(eq(1L), any())).thenReturn(dto);

        mockMvc.perform(post("/api/v1/ventas/1/finalizar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.medioPago").value("CREDITO"));
    }

    @Test
    void testRealizarCompraOnline() throws Exception {
        CompraOnlineRequest request = new CompraOnlineRequest();
        request.setIdOrden(1L);
        request.setIdCliente(1L);
        request.setIdSucursal(1L);
        request.setMedioPago(MedioPago.CREDITO);
        CompraOnlineRequest.ItemCarritoDTO item = new CompraOnlineRequest.ItemCarritoDTO();
        item.setIdLibro(10L);
        item.setCantidad(2);
        request.setItems(List.of(item));

        VentasDTO dto = new VentasDTO();
        dto.setId(1L);
        dto.setTipoVenta(TipoVenta.VENTA_WEB);

        when(ventasService.realizarCompraOnline(any())).thenReturn(dto);

        mockMvc.perform(post("/api/v1/ventas/online")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tipoVenta").value("VENTA_WEB"));
    }

    @Test
    void testListarVentas() throws Exception {
        List<VentasDTO> list = List.of(new VentasDTO(), new VentasDTO());

        when(ventasService.listarVentas()).thenReturn(list);

        mockMvc.perform(get("/api/v1/ventas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testObtenerVenta() throws Exception {
        VentasDTO dto = new VentasDTO();
        dto.setId(1L);

        when(ventasService.obtenerVenta(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/ventas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}
