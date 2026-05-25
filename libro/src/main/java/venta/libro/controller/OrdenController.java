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

import venta.libro.model.EstadoOrden;
import venta.libro.model.OrdenVenta;
import venta.libro.service.OrdenService;

@RestController
@RequestMapping("api/v1/ordenes")
public class OrdenController {
    @Autowired
    private OrdenService ordenService;

    @PostMapping()
    public OrdenVenta postOrdenVenta(@RequestBody OrdenVenta ordenVenta) {
        return ordenService.crear(ordenVenta);
    }
    @GetMapping()
    public List<OrdenVenta> getOrdenesVenta() {
        return ordenService.listar();
    }
    @PutMapping("{id}")
    public OrdenVenta putOrdenVenta(@PathVariable Long id, @RequestBody OrdenVenta ordenVenta) {
        return ordenService.modificar(id, ordenVenta);
    }
    @DeleteMapping("{id}")
    public void deleteOrdenVenta(@PathVariable Long id) {
        ordenService.eliminar(id);
    }

    @PutMapping("{id}/calcularTotal")
    public Double calcularTotal(@PathVariable Long id) {
        return ordenService.calcularTotal(id);
    }
    @PutMapping("{id}/cambiarEstado")
    public OrdenVenta cambiarEstado(@PathVariable Long id, @RequestParam EstadoOrden nuevoEstado) {
        return ordenService.cambiarEstado(id, nuevoEstado);
    }
    @PostMapping("{id}/confirmarPago")
    public boolean confirmarPago(@PathVariable Long id) {
        return ordenService.confirmarPago(id);
    }
    @PutMapping("{id}/cancelar")
    public OrdenVenta cancelarOrden(@PathVariable Long id) {
        return ordenService.cancelarOrden(id);
    }
    @GetMapping("{id}/factura")
    public String generarFactura(@PathVariable Long id) {
        return ordenService.generarFactura(id);
    }
}

