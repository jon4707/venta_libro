package venta.libro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import venta.libro.model.Cantidad;

@Repository
public interface CantidadRepository extends JpaRepository<Cantidad, Long> {

    List<Cantidad> findByIdVenta(Long idVenta);

    Optional<Cantidad> findByIdVentaAndIdLibro(Long idVenta, Long idLibro);

    void deleteByIdVentaAndIdLibro(Long idVenta, Long idLibro);
}
