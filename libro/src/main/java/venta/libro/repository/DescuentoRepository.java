package venta.libro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import venta.libro.model.Descuento;

@Repository
public interface DescuentoRepository extends JpaRepository<Descuento, Long> {

    Optional<Descuento> findByIdAndEstadoTrue(Long id);
}
