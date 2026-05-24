package venta.libro.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import venta.libro.model.EstadoOrden;
import venta.libro.model.OrdenVenta;
import venta.libro.repository.OrdenRepository;

@Service
@Transactional
public class OrdenService {
    @Autowired
    private OrdenRepository ordenRepository;

public OrdenVenta crear(OrdenVenta ordenVenta) {
        ordenVenta.setEstadoOrden(EstadoOrden.pendiente);
        return ordenRepository.save(ordenVenta);
    }

    public List<OrdenVenta> listar() {
        return ordenRepository.findAll();
    }
    public OrdenVenta modificar(Long id, OrdenVenta ordenVenta) {
        OrdenVenta existente = ordenRepository.findById(id).orElse(null);
        if (existente != null) {
            existente.setIdUsuario(ordenVenta.getIdUsuario());
            existente.setFechaOrden(ordenVenta.getFechaOrden());
            existente.setSubtotal(ordenVenta.getSubtotal());
            existente.setImpuestos(ordenVenta.getImpuestos());
            existente.setTotal(ordenVenta.getTotal());
            existente.setDireccionEnvio(ordenVenta.getDireccionEnvio());
            existente.setDireccionFacturacion(ordenVenta.getDireccionFacturacion());
            existente.setEstadoOrden(ordenVenta.getEstadoOrden());
            return ordenRepository.save(existente);
        }
        return null;
    }
    public void eliminar(Long id) {
        ordenRepository.deleteById(id);
    }
    public Double calcularTotal(Long id) {
        OrdenVenta orden = ordenRepository.findById(id).orElse(null);
        if (orden != null) {
            Double resultado = orden.getSubtotal() + orden.getImpuestos();
            orden.setTotal(resultado);
            ordenRepository.save(orden);
            return resultado;
        }
        return 0.0;
    }

    public OrdenVenta cambiarEstado(Long id, EstadoOrden nuevoEstado) {
        OrdenVenta orden = ordenRepository.findById(id).orElse(null);
        if (orden != null) {
            orden.setEstadoOrden(nuevoEstado);
            return ordenRepository.save(orden);
        }
        return null;
    }

    public boolean confirmarPago(Long id) {
        OrdenVenta orden = ordenRepository.findById(id).orElse(null);
        if (orden != null) {
            orden.setEstadoOrden(EstadoOrden.procesando);
            ordenRepository.save(orden);
            return true;
        }
        return false;
    }

    public OrdenVenta cancelarOrden(Long id) {
        OrdenVenta orden = ordenRepository.findById(id).orElse(null);
        if (orden != null) {
            orden.setEstadoOrden(EstadoOrden.cancelado);
            return ordenRepository.save(orden);
        }
        return null;
    }

    public String generarFactura(Long id) {
        OrdenVenta orden = ordenRepository.findById(id).orElse(null);
        if (orden != null) {
            return "Factura Orden Nro: " + orden.getId() + 
                   " - Total: $" + orden.getTotal() + 
                   " - Estado actual: " + orden.getEstadoOrden();
        }
        return "Orden no encontrada";
    }
}


