package venta.libro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import venta.libro.model.EstadoOrden;
import venta.libro.model.EstadoVenta;

public interface EstadoVentaRepository extends JpaRepository<EstadoVenta,Long>{
    Optional<EstadoVenta> findByNombre(String nombre);
}
