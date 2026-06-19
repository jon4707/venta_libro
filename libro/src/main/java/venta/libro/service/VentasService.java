package venta.libro.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import venta.libro.model.Ventas;
import venta.libro.repository.VentasRepository;

@Service
@Transactional
public class VentasService {

    @Autowired 
    private VentasRepository ventasRepository;

    public Ventas crearVentas (Ventas venta){
        return ventasRepository.save(venta);
    }

    public List<Ventas> listarVentas(){
        return ventasRepository.findAll();
    }

    public Ventas modificar(Long id, Ventas venta) {
        Ventas existente = ventasRepository.findById(id).orElse(null);
        if (existente != null) {
            existente.setFechaCompra(venta.getFechaCompra());
            existente.setSubtotal(venta.getSubtotal());
            existente.setDescuentoTotal(venta.getDescuentoTotal());
            existente.setImpuestos(venta.getImpuestos());
            existente.setTotal(venta.getTotal());
            existente.setMedioPago(venta.getMedioPago());
            existente.setTipoVenta(venta.getTipoVenta());
            existente.setVuelto(venta.getVuelto());
            existente.setIdCliente(venta.getIdCliente());
            return ventasRepository.save(existente);
        }
        return null;
    }

    public void eliminar(Long id) {
        ventasRepository.deleteById(id);
    }
    
}
