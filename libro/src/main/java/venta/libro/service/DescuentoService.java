package venta.libro.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import venta.libro.dto.CrearDescuentosRequest;
import venta.libro.dto.DescuentoDTO;
import venta.libro.model.Descuento;
import venta.libro.repository.DescuentoRepository;

@Service
@Transactional
public class DescuentoService {

    @Autowired
    private DescuentoRepository descuentoRepository;

    private DescuentoDTO toDTO(Descuento d) {
        return new DescuentoDTO(
            d.getId(), d.getNombre(), d.getFechaCreacion(),
            d.getFechaVencimiento(), d.getPorcentaje(), d.getEstado()
        );
    }

    public List<DescuentoDTO> crearDescuentos(CrearDescuentosRequest request) {
        if (request.getFechaVencimiento().before(new Date())) {
            throw new RuntimeException("La fecha de vencimiento debe ser futura");
        }
        return java.util.stream.IntStream.range(0, request.getCantidad())
                .mapToObj(i -> {
                    Descuento d = new Descuento();
                    d.setNombre(request.getNombre());
                    d.setFechaCreacion(new Date());
                    d.setFechaVencimiento(request.getFechaVencimiento());
                    d.setPorcentaje(request.getPorcentaje());
                    d.setEstado(true);
                    return toDTO(descuentoRepository.save(d));
                })
                .collect(Collectors.toList());
    }

    public List<DescuentoDTO> listarDescuentos() {
        return descuentoRepository.findAll().stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    public DescuentoDTO obtenerDescuento(Long id) {
        return descuentoRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Descuento no encontrado"));
    }
}
