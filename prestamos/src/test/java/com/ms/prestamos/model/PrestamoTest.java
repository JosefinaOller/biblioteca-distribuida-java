package com.ms.prestamos.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas de la capa de Entidad para Prestamo")
class PrestamoTest {

    @Test
    @DisplayName("Debe crear la instancia de Prestamo usando el constructor con los argumentos")
    void createPrestamoWithAllArgs() {
        Long existingId = 1L;
        Long existingIdUsuario = 10L;
        Long existingIdLibro = 20L;
        LocalDate existingFechaPrestamo = LocalDate.now();
        LocalDate existingFechaDevolucion = LocalDate.now().plusDays(7);

        Prestamo prestamo = new Prestamo(existingId, existingIdUsuario, existingIdLibro, existingFechaPrestamo, existingFechaDevolucion);

        assertNotNull(prestamo, "Prestamo no debe ser nulo.");
        assertEquals(existingId, prestamo.getId(), "El ID debe coincidir.");
        assertEquals(existingIdUsuario, prestamo.getIdUsuario(), "El ID de Usuario debe coincidir.");
        assertEquals(existingIdLibro, prestamo.getIdLibro(), "El ID de Libro debe coincidir.");
        assertEquals(existingFechaPrestamo, prestamo.getFechaPrestamo(), "La fecha de prestamo debe coincidir.");
        assertEquals(existingFechaDevolucion, prestamo.getFechaDevolucion(), "La fecha de devolucion debe coincidir.");
    }

    @Test
    @DisplayName("Debe crear la instancia de Prestamo usando el constructor vacío")
    void createPrestamoWithNoArgs() {
        Prestamo prestamo = new Prestamo();
        Long existingId = 2L;
        Long existingIdUsuario = 15L;
        Long existingIdLibro = 25L;
        LocalDate existingFechaPrestamo = LocalDate.now();
        LocalDate existingFechaDevolucion = null;

        prestamo.setId(existingId);
        prestamo.setIdUsuario(existingIdUsuario);
        prestamo.setIdLibro(existingIdLibro);
        prestamo.setFechaPrestamo(existingFechaPrestamo);
        prestamo.setFechaDevolucion(existingFechaDevolucion);

        assertNotNull(prestamo, "Prestamo no debe ser nulo.");
        assertEquals(existingId, prestamo.getId(), "El ID debe coincidir.");
        assertEquals(existingIdUsuario, prestamo.getIdUsuario(), "El ID de Usuario debe coincidir.");
        assertEquals(existingIdLibro, prestamo.getIdLibro(), "El ID de Libro debe coincidir.");
        assertEquals(existingFechaPrestamo, prestamo.getFechaPrestamo(), "La fecha de prestamo debe coincidir.");
        assertEquals(existingFechaDevolucion, prestamo.getFechaDevolucion(), "La fecha de devolucion debe coincidir.");
    }

    @Test
    @DisplayName("Debe inicializar el ID como nulo usando el constructor NoArgsConstructor")
    void initializeIdAsNull() {
        Prestamo prestamo = new Prestamo();
        assertNull(prestamo.getId(), "El ID debe ser nulo al usar el constructor vacio.");
    }
}
