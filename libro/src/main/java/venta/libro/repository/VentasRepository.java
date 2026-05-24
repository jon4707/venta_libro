package venta.libro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import venta.libro.model.Ventas;

public interface VentasRepository extends JpaRepository<Ventas, Long> {     
}
