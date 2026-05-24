package venta.libro.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import venta.libro.model.OrdenVenta;
import venta.libro.model.Ventas;
import venta.libro.repository.VentasRepository;

@Service
@Transactional
public class VentasService {
    @Autowired
    private VentasRepository ventasRepository;

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

            if (nuevaVenta.getOrdenventas() != null) {

                existente.getOrdenventas().clear();  

                for (OrdenVenta orden : nuevaVenta.getOrdenventas()) {
                    orden.setVenta(existente); 
                    existente.getOrdenventas().add(orden); 
                }
            } else {
                
                existente.getOrdenventas().clear();
            }
            
            return ventasRepository.save(existente);
        }
    
        return null;
    }
    public void eliminar(Long id) {
        ventasRepository.deleteById(id);
    }
}
    

