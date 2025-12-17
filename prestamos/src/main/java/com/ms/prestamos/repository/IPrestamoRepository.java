package com.ms.prestamos.repository;

import com.ms.prestamos.model.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPrestamoRepository extends JpaRepository<Prestamo, Long> {
}
