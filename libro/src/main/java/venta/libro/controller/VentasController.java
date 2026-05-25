package venta.libro.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import venta.libro.model.OrdenVenta;
import venta.libro.model.Ventas;
import venta.libro.service.VentasService;

@RestController
@RequestMapping("api/v1/ventas")
public class VentasController {
    @Autowired
    private VentasService ventasService;
    @PostMapping
    public Ventas postVenta(@Valid @RequestBody Ventas ventas) {
        return ventasService.crear(ventas);
    }
    @GetMapping
    public List<Ventas> getVentas() {
        return ventasService.listar();
    }
    @PutMapping("/{id}")
    public Ventas putVenta(@PathVariable Long id,@Valid @RequestBody Ventas ventas) {
        return ventasService.modificar(id, ventas);
    }
    @DeleteMapping("/{id}")
    public void deleteVenta(@PathVariable Long id) {
        ventasService.eliminar(id);
    }
    @PostMapping("/{id}/productos")
    public void agregarProducto(@PathVariable Long id, @Valid @RequestBody OrdenVenta ordenVenta) {
        ventasService.agregarProducto(id, ordenVenta);
    }

    @DeleteMapping("/{id}/productos/{ordenVentaId}")
    public void eliminarProducto(@PathVariable Long id, @PathVariable Long ordenVentaId) {
        ventasService.eliminarProducto(id, ordenVentaId);
    }
    @PostMapping("/{id}/pagar")
    public void procesarPago(
            @PathVariable Long id, 
            @RequestParam Long idMedioPago,
            @RequestParam Double dineroRecibido) {
        ventasService.procesarPago(id, idMedioPago, dineroRecibido);
    }
    @PutMapping("/{id}/estado")
    public void cambiarEstado(
            @PathVariable Long id, 
            @RequestParam Long idNuevoEstado) {
        ventasService.cambiarEstado(id, idNuevoEstado);
    }
    @PutMapping("/{id}/cliente")
    public void asignarCliente(@PathVariable Long id, @RequestParam Long idCliente) {
        ventasService.asignarCliente(id, idCliente);
    }
}
