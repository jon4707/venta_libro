package venta.libro.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import venta.libro.dto.CrearDescuentosRequest;
import venta.libro.dto.DescuentoDTO;
import venta.libro.service.DescuentoService;

@WebMvcTest(DescuentoController.class)
class DescuentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DescuentoService descuentoService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void crearDescuentos_deberiaRetornar201() throws Exception {
        CrearDescuentosRequest request = new CrearDescuentosRequest();
        request.setNombre("Cyber Monday");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 15);
        request.setFechaVencimiento(cal.getTime());
        request.setPorcentaje(25);
        request.setCantidad(2);

        DescuentoDTO dto1 = new DescuentoDTO(1L, "Cyber Monday", new Date(), cal.getTime(), 25, true);
        DescuentoDTO dto2 = new DescuentoDTO(2L, "Cyber Monday", new Date(), cal.getTime(), 25, true);

        when(descuentoService.crearDescuentos(any(CrearDescuentosRequest.class)))
                .thenReturn(List.of(dto1, dto2));

        mockMvc.perform(post("/api/v1/descuentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Cyber Monday"))
                .andExpect(jsonPath("$[0].porcentaje").value(25));
    }

    @Test
    void listarDescuentos_deberiaRetornar200() throws Exception {
        DescuentoDTO dto = new DescuentoDTO(1L, "Descuento A", new Date(), new Date(), 10, true);
        when(descuentoService.listarDescuentos()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/descuentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Descuento A"));
    }

    @Test
    void obtenerDescuento_deberiaRetornar200() throws Exception {
        DescuentoDTO dto = new DescuentoDTO(1L, "VIP", new Date(), new Date(), 20, true);
        when(descuentoService.obtenerDescuento(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/descuentos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("VIP"));
    }

    @Test
    void listarDescuentos_vacia_deberiaRetornar200Vacio() throws Exception {
        when(descuentoService.listarDescuentos()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/descuentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
