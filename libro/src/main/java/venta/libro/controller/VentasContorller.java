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
import org.springframework.web.bind.annotation.RestController;

import venta.libro.model.Ventas;
import venta.libro.service.VentasService;

@RestController
@RequestMapping("api/v1/ventas")
public class VentasContorller {

    @Autowired
    private VentasService ventasService;

    @PostMapping()
    public Ventas postVenta(@RequestBody Ventas venta) {
        return ventasService.crearVentas(venta);
    }

    @GetMapping()
    public List<Ventas> getVentas() {
        return ventasService.listarVentas();
    }

    @PutMapping("{id}")
    public Ventas putVenta(@PathVariable Long id, @RequestBody Ventas venta) {
        return ventasService.modificar(id, venta);
    }

    @DeleteMapping("{id}")
    public void deleteVenta(@PathVariable Long id) {
        ventasService.eliminar(id);
    }
    
}
