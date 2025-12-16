package com.ms.libros.repository;

import com.ms.libros.model.Libro;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@DisplayName("Pruebas de la capa de Repository para Libro")
class ILibroRepositoryTest {

    @Autowired
    private ILibroRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Debe guardar un Libro y generar su ID")
    void saveLibro() {
        Libro newLibro = new Libro(null, "Clean Code", "Robert C. Martin", "978-0-13-235088-4", 5);

        Libro savedLibro = repository.save(newLibro);

        assertNotNull(savedLibro.getId(), "El ID debe ser generado por JPA.");
        assertEquals("Clean Code", savedLibro.getTitulo());
    }

    @Test
    @DisplayName("Debe encontrar un Libro por ID")
    void findLibroById() {
        Libro libro = new Libro(null, "Harry Potter", "J.K. Rowling", "978-0-7475-3274-3", 10);
        entityManager.persistAndFlush(libro);
        Long idFound = libro.getId();

        Optional<Libro> result = repository.findById(idFound);

        assertTrue(result.isPresent(), "Se debe encontrar el libro.");
        assertEquals("Harry Potter", result.get().getTitulo());
        assertEquals("978-0-7475-3274-3", result.get().getIsbn());
    }

    @Test
    @DisplayName("Debe retornar vacío si el ID no existe")
    void returnEmptyWhenIdNotFound() {
        Long idNotFound = 99L;

        Optional<Libro> result = repository.findById(idNotFound);

        assertTrue(result.isEmpty(), "Debe retornar vacío para un ID que no existe.");
    }

    @Test
    @DisplayName("Debe eliminar un Libro por ID y confirmar su ausencia")
    void deleteLibroById() {
        Libro libroToDelete = new Libro(null, "Libro a Borrar", "Autor X", "978-1-1111-1111-1", 1);
        entityManager.persistAndFlush(libroToDelete);
        Long idToDelete = libroToDelete.getId();

        repository.deleteById(idToDelete);

        Optional<Libro> result = repository.findById(idToDelete);
        assertTrue(result.isEmpty(), "El libro debe haber sido eliminado.");
    }

    @Test
    @DisplayName("existsByIsbn: Retorna TRUE si el ISBN existe en la BD")
    void existsByIsbn_ExistingIsbn_ReturnsTrue() {
        String isbnExisted = "978-1-2345-6789-0";
        Libro libro = new Libro(null, "Java Ninja", "Dev Master", isbnExisted, 5);
        entityManager.persistAndFlush(libro);

        boolean isIsbnPresent = repository.existsByIsbn(isbnExisted);

        assertTrue(isIsbnPresent, "Debe retornar true si el ISBN existe.");
    }

    @Test
    @DisplayName("existsByIsbn: Retorna FALSE si el ISBN no existe en la BD")
    void existsByIsbn_NotExistingIsbn_ReturnsFalse() {
        String isbnNotExisted = "999-0-0000-0000-0";

        boolean isIsbnPresent = repository.existsByIsbn(isbnNotExisted);

        assertFalse(isIsbnPresent, "Debe retornar false si el ISBN no existe.");
    }
}