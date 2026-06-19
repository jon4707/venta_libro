package venta.libro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import venta.libro.model.Ventas;

@Repository
public interface VentasRepository extends JpaRepository<Ventas, Long> {
 

}
