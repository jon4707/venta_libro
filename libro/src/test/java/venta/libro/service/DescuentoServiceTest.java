package venta.libro.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import venta.libro.dto.CrearDescuentosRequest;
import venta.libro.dto.DescuentoDTO;
import venta.libro.model.Descuento;
import venta.libro.repository.DescuentoRepository;

@ExtendWith(MockitoExtension.class)
class DescuentoServiceTest {

    @Mock
    private DescuentoRepository descuentoRepository;

    @InjectMocks
    private DescuentoService descuentoService;

    private Date futureDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 30);
        return cal.getTime();
    }

    private Date pastDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -5);
        return cal.getTime();
    }

    @Test
    void crearDescuentos_deberiaCrearDescuentos() {
        CrearDescuentosRequest request = new CrearDescuentosRequest();
        request.setNombre("Black Friday");
        request.setFechaVencimiento(futureDate());
        request.setPorcentaje(20);
        request.setCantidad(3);

        when(descuentoRepository.save(any(Descuento.class))).thenAnswer(invocation -> {
            Descuento d = invocation.getArgument(0);
            d.setId(1L);
            return d;
        });

        List<DescuentoDTO> result = descuentoService.crearDescuentos(request);

        assertThat(result).hasSize(3);
        assertThat(result.get(0).getNombre()).isEqualTo("Black Friday");
        assertThat(result.get(0).getPorcentaje()).isEqualTo(20);
        assertThat(result.get(0).getEstado()).isTrue();
        verify(descuentoRepository, times(3)).save(any(Descuento.class));
    }

    @Test
    void crearDescuentos_fechaVencimientoPasada_deberiaLanzarExcepcion() {
        CrearDescuentosRequest request = new CrearDescuentosRequest();
        request.setNombre("Expired");
        request.setFechaVencimiento(pastDate());
        request.setPorcentaje(10);
        request.setCantidad(1);

        assertThatThrownBy(() -> descuentoService.crearDescuentos(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("La fecha de vencimiento debe ser futura");
    }

    @Test
    void listarDescuentos_deberiaRetornarLista() {
        Descuento d1 = new Descuento();
        d1.setId(1L);
        d1.setNombre("Desc 1");
        d1.setPorcentaje(10);
        d1.setEstado(true);
        Descuento d2 = new Descuento();
        d2.setId(2L);
        d2.setNombre("Desc 2");
        d2.setPorcentaje(25);
        d2.setEstado(true);

        when(descuentoRepository.findAll()).thenReturn(List.of(d1, d2));

        List<DescuentoDTO> result = descuentoService.listarDescuentos();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getNombre()).isEqualTo("Desc 1");
        assertThat(result.get(1).getNombre()).isEqualTo("Desc 2");
    }

    @Test
    void listarDescuentos_vacia_deberiaRetornarListaVacia() {
        when(descuentoRepository.findAll()).thenReturn(List.of());

        List<DescuentoDTO> result = descuentoService.listarDescuentos();

        assertThat(result).isEmpty();
    }

    @Test
    void obtenerDescuento_encontrado_deberiaRetornarDTO() {
        Descuento d = new Descuento();
        d.setId(1L);
        d.setNombre("Descuento VIP");
        d.setPorcentaje(15);
        d.setEstado(true);

        when(descuentoRepository.findById(1L)).thenReturn(Optional.of(d));

        DescuentoDTO result = descuentoService.obtenerDescuento(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNombre()).isEqualTo("Descuento VIP");
        assertThat(result.getPorcentaje()).isEqualTo(15);
    }

    @Test
    void obtenerDescuento_noEncontrado_deberiaLanzarExcepcion() {
        when(descuentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> descuentoService.obtenerDescuento(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Descuento no encontrado");
    }
}
