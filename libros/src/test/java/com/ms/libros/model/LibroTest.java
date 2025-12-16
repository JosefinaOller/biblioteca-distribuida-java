package com.ms.libros.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.AssertionsKt.assertNotNull;

@DisplayName("Pruebas de la entidad Libro")
class LibroTest {

    @Test
    @DisplayName("Debe crear la instancia de Libro usando el constructor con los argumentos")
    void  createLibroWithAllArgs() {
        Long existingId = 1L;
        String existingTitulo = "Tuya";
        String existingAutor = "María Inés";
        String existingISBN = "980-852-7415-157";
        int existingEjemplares = 10;

        Libro libro = new Libro(existingId,existingTitulo,existingAutor,existingISBN,existingEjemplares);

        assertNotNull(libro,"Libro no debe ser nulo.");
        assertEquals(existingId,libro.getId(),"El ID debe coincidir.");
        assertEquals(existingTitulo,libro.getTitulo(),"El titulo debe coincidir.");
        assertEquals(existingAutor,libro.getAutor(),"El autor debe coincidir.");
        assertEquals(existingISBN,libro.getIsbn(),"El ISBN debe coincidir.");
        assertEquals(existingEjemplares,libro.getEjemplaresDisponibles(),"La cantidad de ejemplares disponibles debe coincidir.");

    }

    @Test
    @DisplayName("Debe crear la instancia de Libro usando el constructor vacío")
    void  createLibroWithNoArgs() {

        Libro libro = new Libro();
        Long existingId = 2L;
        String existingTitulo = "Messi es el mejor";
        String existingAutor = "Lionel Messi";
        String existingISBN = "980-999-7415-157";
        int existingEjemplares = 2;

        libro.setId(existingId);
        libro.setTitulo(existingTitulo);
        libro.setAutor(existingAutor);
        libro.setIsbn(existingISBN);
        libro.setEjemplaresDisponibles(existingEjemplares);

        assertNotNull(libro,"Libro no debe ser nulo.");
        assertEquals(existingId,libro.getId(),"El ID debe coincidir.");
        assertEquals(existingTitulo,libro.getTitulo(),"El titulo debe coincidir.");
        assertEquals(existingAutor,libro.getAutor(),"El autor debe coincidir.");
        assertEquals(existingISBN,libro.getIsbn(),"El ISBN debe coincidir.");
        assertEquals(existingEjemplares,libro.getEjemplaresDisponibles(),"La cantidad de ejemplares disponibles debe coincidir.");

    }

    @Test
    @DisplayName("Debe inicializar el ID como nulo usando el constructor NoArgsConstructor")
    void initializeIdAsNull() {
        Libro libro = new Libro();
        assertNull(libro.getId(),"El ID debe ser nulo al usar el constructor vacio.");
    }

}
