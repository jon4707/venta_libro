package venta.libro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import venta.libro.model.MedioPago;

public interface MedioPagorRepository extends JpaRepository <MedioPago,Long> {
    Optional<MedioPago> findByNombre(String nombre);
}
