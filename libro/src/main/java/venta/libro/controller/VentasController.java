package venta.libro.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import venta.libro.dto.AgregarProductoRequest;
import venta.libro.dto.CantidadDTO;
import venta.libro.dto.CompraOnlineRequest;
import venta.libro.dto.CrearVentaRequest;
import venta.libro.dto.FinalizarCompraRequest;
import venta.libro.dto.VentasDTO;
import venta.libro.service.VentasService;

@RestController
@RequestMapping("api/v1/ventas")
public class VentasController {

    @Autowired
    private VentasService ventasService;

    @PostMapping("/crear")
    public ResponseEntity<VentasDTO> crearVenta(@Valid @RequestBody CrearVentaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ventasService.crearVentaCajero(request));
    }

    @PostMapping("/{id}/productos")
    public ResponseEntity<CantidadDTO> añadirProducto(
            @PathVariable Long id,
            @Valid @RequestBody AgregarProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ventasService.añadirProducto(id, request));
    }

    @DeleteMapping("/{idVenta}/productos/{idLibro}")
    public ResponseEntity<Void> eliminarProducto(
            @PathVariable Long idVenta,
            @PathVariable Long idLibro) {
        ventasService.eliminarProducto(idVenta, idLibro);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/finalizar")
    public ResponseEntity<VentasDTO> finalizarCompra(
            @PathVariable Long id,
            @Valid @RequestBody FinalizarCompraRequest request) {
        return ResponseEntity.ok(ventasService.finalizarCompra(id, request));
    }

    @PostMapping("/online")
    public ResponseEntity<VentasDTO> realizarCompraOnline(@Valid @RequestBody CompraOnlineRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ventasService.realizarCompraOnline(request));
    }

    @GetMapping
    public ResponseEntity<List<VentasDTO>> listarVentas() {
        return ResponseEntity.ok(ventasService.listarVentas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentasDTO> obtenerVenta(@PathVariable Long id) {
        return ResponseEntity.ok(ventasService.obtenerVenta(id));
    }
}
