package venta.libro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import venta.libro.model.OrdenVenta;

public interface OrdenRepository extends JpaRepository<OrdenVenta,Long> {
    
}
