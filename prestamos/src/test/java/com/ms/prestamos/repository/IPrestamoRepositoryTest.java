package com.ms.prestamos.repository;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import com.ms.prestamos.model.Prestamo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("Pruebas de la capa de Repository para Prestamo")
class IPrestamoRepositoryTest {

    @Autowired
    private IPrestamoRepository repository;
    private Prestamo prestamo;

    @BeforeEach
    void setUp() {
        prestamo = new Prestamo();
        prestamo.setIdUsuario(1L);
        prestamo.setIdLibro(10L);
        prestamo.setFechaPrestamo(LocalDate.now());
    }

    @Test
    @DisplayName("Test repository: guardar y buscar préstamo por ID")
    void testSaveAndFindById() {
        Prestamo saved = repository.save(prestamo);
        Optional<Prestamo> found = repository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals(1L, found.get().getIdUsuario());
    }

    @Test
    @DisplayName("Test repository: listar todos los préstamos")
    void testFindAll() {
        repository.save(prestamo);

        Prestamo otroPrestamo = new Prestamo();
        otroPrestamo.setIdUsuario(2L);
        otroPrestamo.setIdLibro(5L);
        otroPrestamo.setFechaPrestamo(LocalDate.now());
        repository.save(otroPrestamo);

        List<Prestamo> lista = repository.findAll();

        assertEquals(2, lista.size());
    }

    @Test
    @DisplayName("Test repository: actualizar fecha de devolución")
    void testUpdatePrestamo() {
        Prestamo saved = repository.save(prestamo);
        saved.setFechaDevolucion(LocalDate.now());

        Prestamo updated = repository.save(saved);

        assertNotNull(updated.getFechaDevolucion());
        assertEquals(LocalDate.now(), updated.getFechaDevolucion());
    }

    @Test
    @DisplayName("Test repository: buscar ID inexistente retorna vacío")
    void testFindByIdNotFound() {
        Optional<Prestamo> found = repository.findById(999L);
        assertTrue(found.isEmpty());
    }
}

