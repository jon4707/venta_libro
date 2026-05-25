package venta.libro.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import venta.libro.model.EstadoVenta;
import venta.libro.model.MedioPago;
import venta.libro.model.OrdenVenta;
import venta.libro.model.Ventas;
import venta.libro.repository.EstadoVentaRepository;
import venta.libro.repository.MedioPagorRepository;
import venta.libro.repository.VentasRepository;

@Service
@Transactional
public class VentasService {
    @Autowired
    private VentasRepository ventasRepository;
    @Autowired
    private MedioPagorRepository medioPagoRepository;
    @Autowired
    private EstadoVentaRepository estadoVentaRepository;

    public Ventas crear(Ventas ventas) {
        return ventasRepository.save(ventas);
    }

    public List<Ventas> listar() {
        return ventasRepository.findAll();
    }
    
    public Ventas modificar(Long id, Ventas nuevaVenta) {
        Ventas existente = ventasRepository.findById(id).orElse(null);
        if (existente != null) {
            existente.setSubtotal(nuevaVenta.getSubtotal());
            existente.setDescuentoTotal(nuevaVenta.getDescuentoTotal());
            existente.setTotal(nuevaVenta.getTotal());
            existente.setVuelto(nuevaVenta.getVuelto());
            existente.setMedioPago(nuevaVenta.getMedioPago());
            existente.setEstadoVenta(nuevaVenta.getEstadoVenta());
            existente.setIdCliente(nuevaVenta.getIdCliente());

            existente.getOrdenventas().clear();
            for (OrdenVenta orden : nuevaVenta.getOrdenventas()) {
                orden.setVenta(existente);
                existente.getOrdenventas().add(orden);
            }
            return ventasRepository.save(existente);
        }
        return null;
    }
    public void eliminar(Long id) {
        ventasRepository.deleteById(id);
    }
    public void agregarProducto(Long ventaId, OrdenVenta ordenVenta) {
        Ventas venta = ventasRepository.findById(ventaId).orElse(null);
        if (venta != null) {
            ordenVenta.setVenta(venta);
            venta.getOrdenventas().add(ordenVenta);
            recargarMontosVenta(venta);
            ventasRepository.save(venta);
        }
    }
    public void eliminarProducto(Long ventaId, Long ordenVentaId) {
        Ventas venta = ventasRepository.findById(ventaId).orElse(null);
        if (venta != null) {
            venta.getOrdenventas().removeIf(orden -> orden.getId().equals(ordenVentaId));
            recargarMontosVenta(venta);
            ventasRepository.save(venta);
        }
    }
    public Double calcularSubtotal(Ventas venta) {
        Double subtotal = 0.0;
        if (venta.getOrdenventas() != null) {
            for (OrdenVenta orden : venta.getOrdenventas()) {

                if (orden.getTotal() != null) {
                    subtotal += orden.getTotal();
                }
            }
        }
        return subtotal;
    }
    public Double calcularDescuento(Ventas venta) {
        return 0.0;
    }
    public Double calcularTotal(Ventas venta) {
        return calcularSubtotal(venta) - calcularDescuento(venta);
    }
    public void procesarPago(Long ventaId, Long idMedioPago, Double dineroRecibido) throws IllegalAccessException {
        Ventas venta = ventasRepository.findById(ventaId).orElse(null);
        MedioPago medio = medioPagoRepository.findById(idMedioPago).orElse(null);
        if (venta != null && medio != null) {
            if (dineroRecibido<venta.getTotal()){
                throw new IllegalAccessException("El dinero es insuficiente para la venta");
            }
            venta.setMedioPago(medio);
            venta.setVuelto(calcularVuelto(dineroRecibido, venta.getTotal()));
            ventasRepository.save(venta);
          
        }else{
            throw new EntityNotFoundException("venta o miedo de pago no enocntrado");   
        }
    }
    public Double calcularVuelto(Double dineroRecibido, Double totalVenta) {
        return dineroRecibido - totalVenta;
    }
    public void cambiarEstado(Long ventaId, Long idNuevoEstado) {
        Ventas venta = ventasRepository.findById(ventaId).orElse(null);
        EstadoVenta estado = estadoVentaRepository.findById(idNuevoEstado).orElse(null);

        if (venta != null && estado != null) {
            venta.setEstadoVenta(estado);
            ventasRepository.save(venta);
        }
    }
    public void asignarCliente(Long ventaId, Long idCliente) {
        Ventas venta = ventasRepository.findById(ventaId).orElse(null);
        if (venta != null) {
            venta.setIdCliente(idCliente);
            ventasRepository.save(venta);
        }
    }
    private void recargarMontosVenta(Ventas venta) {
        venta.setSubtotal(calcularSubtotal(venta));
        venta.setDescuentoTotal(calcularDescuento(venta));
        venta.setTotal(calcularTotal(venta));
    }
}
    

