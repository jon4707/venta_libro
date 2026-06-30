package venta.libro.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import venta.libro.dto.CrearDescuentosRequest;
import venta.libro.dto.DescuentoDTO;
import venta.libro.service.DescuentoService;

@RestController
@RequestMapping("api/v1/descuentos")
public class DescuentoController {

    @Autowired
    private DescuentoService descuentoService;

    @PostMapping
    public ResponseEntity<List<DescuentoDTO>> crearDescuentos(@Valid @RequestBody CrearDescuentosRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(descuentoService.crearDescuentos(request));
    }

    @GetMapping
    public ResponseEntity<List<DescuentoDTO>> listarDescuentos() {
        return ResponseEntity.ok(descuentoService.listarDescuentos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DescuentoDTO> obtenerDescuento(@PathVariable Long id) {
        return ResponseEntity.ok(descuentoService.obtenerDescuento(id));
    }
}
