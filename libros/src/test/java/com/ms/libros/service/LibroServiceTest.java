package com.ms.libros.service;

import com.ms.libros.exception.IsbnExistenteException;
import com.ms.libros.exception.LibroNoEncontradoException;
import com.ms.libros.model.Libro;
import com.ms.libros.repository.ILibroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de la capa Service para Libro")
class LibroServiceTest {

    @InjectMocks
    private LibroService service;

    @Mock
    private ILibroRepository repository;

    private Libro existingLibro;
    private final Long idExisted = 1L;
    private final Long idNotExisted = 99L;
    private final String isbnExisted = "978-1234-567-890";
    private final String isbnNew = "978-9876-543-210";

    @BeforeEach
    void setUp() {
        existingLibro = new Libro(idExisted, "Java a Fondo", "Pablo Augusto", isbnExisted, 10);
    }

    @Test
    @DisplayName("Debe guardar un Libro si el ISBN no existe")
    void saveLibro_WithNewIsbn_SavesSuccessfully() throws IsbnExistenteException {
        Libro newLibro = new Libro(null, "Spring Boot Action", "Craig Walls", isbnNew, 5);
        Libro savedLibro = new Libro(2L, "Spring Boot Action", "Craig Walls", isbnNew, 5);

        when(repository.existsByIsbn(newLibro.getIsbn())).thenReturn(false);
        when(repository.save(any(Libro.class))).thenReturn(savedLibro);

        Libro result = service.save(newLibro);

        assertNotNull(result, "El libro guardado no debe ser nulo.");
        assertEquals(isbnNew, result.getIsbn(), "El ISBN debe coincidir.");
        assertEquals(2L, result.getId(), "El ID debe ser el generado.");

        verify(repository, times(1)).existsByIsbn(newLibro.getIsbn());
        verify(repository, times(1)).save(newLibro);
    }

    @Test
    @DisplayName("Debe lanzar IsbnExistenteException al guardar si el ISBN ya está registrado")
    void saveLibro_DuplicateIsbn_ThrowsException() {
        Libro duplicateLibro = new Libro(null, "Intento de Copia", "Autor X", isbnExisted, 1);

        when(repository.existsByIsbn(isbnExisted)).thenReturn(true);

        assertThrows(IsbnExistenteException.class, () -> service.save(duplicateLibro), "Debe lanzar IsbnExistenteException cuando el ISBN ya existe.");

        verify(repository, times(1)).existsByIsbn(isbnExisted);
        verify(repository, never()).save(any(Libro.class));
    }

    @Test
    @DisplayName("Debe retornar un Libro existente por ID")
    void findLibroById_ExistingId_ReturnsLibro() throws LibroNoEncontradoException {
        when(repository.findById(idExisted)).thenReturn(Optional.of(existingLibro));

        Libro result = service.findById(idExisted);

        assertNotNull(result, "El libro retornado no debe ser nulo.");
        assertEquals(idExisted, result.getId(), "El ID debe coincidir.");
        assertEquals("Java a Fondo", result.getTitulo(), "El título debe coincidir.");

        verify(repository, times(1)).findById(idExisted);
    }

    @Test
    @DisplayName("Debe lanzar LibroNoEncontradoException si el ID no existe")
    void findLibroById_NonExistentId_ThrowsException() {
        when(repository.findById(idNotExisted)).thenReturn(Optional.empty());

        assertThrows(LibroNoEncontradoException.class, () -> service.findById(idNotExisted), "Debe lanzar excepción si el libro no se encuentra.");

        verify(repository, times(1)).findById(idNotExisted);
    }

    @Test
    @DisplayName("Debe retornar una lista de Libros")
    void getAll_ReturnsListOfLibros() {
        Libro libro2 = new Libro(2L, "Otro Libro", "Otro Autor", isbnNew, 5);
        List<Libro> listMock = Arrays.asList(existingLibro, libro2);

        when(repository.findAll()).thenReturn(listMock);

        List<Libro> result = service.getAll();

        assertNotNull(result, "La lista no debe ser nula.");
        assertEquals(2, result.size(), "Debe retornar 2 libros.");
        assertEquals(isbnExisted, result.get(0).getIsbn());

        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Update: Debe actualizar correctamente si el ISBN no cambia")
    void updateLibro_SameIsbn_UpdatesSuccessfully() throws LibroNoEncontradoException, IsbnExistenteException {
        Libro libroUpdate = new Libro(idExisted, "Titulo Nuevo", "Autor Nuevo", isbnExisted, 20);

        when(repository.findById(idExisted)).thenReturn(Optional.of(existingLibro));
        when(repository.save(any(Libro.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Libro result = service.update(libroUpdate);

        assertEquals("Titulo Nuevo", result.getTitulo());
        assertEquals(isbnExisted, result.getIsbn());

        verify(repository, never()).existsByIsbn(anyString());
        verify(repository, times(1)).save(any(Libro.class));
    }

    @Test
    @DisplayName("Update: Debe actualizar correctamente si cambia a un ISBN nuevo válido")
    void updateLibro_NewValidIsbn_UpdatesSuccessfully() throws LibroNoEncontradoException, IsbnExistenteException {
        Libro libroUpdate = new Libro(idExisted, "Titulo Nuevo", "Autor Nuevo", isbnNew, 20);

        when(repository.findById(idExisted)).thenReturn(Optional.of(existingLibro));
        when(repository.existsByIsbn(isbnNew)).thenReturn(false);
        when(repository.save(any(Libro.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Libro result = service.update(libroUpdate);

        assertEquals(isbnNew, result.getIsbn());

        verify(repository, times(1)).existsByIsbn(isbnNew);
        verify(repository, times(1)).save(any(Libro.class));
    }

    @Test
    @DisplayName("Update: Debe lanzar IsbnExistenteException si cambia a un ISBN ocupado")
    void updateLibro_DuplicateIsbn_ThrowsException() {
        Libro libroUpdate = new Libro(idExisted, "Titulo", "Autor", "999-9999-999-999", 1);

        when(repository.findById(idExisted)).thenReturn(Optional.of(existingLibro));
        when(repository.existsByIsbn("999-9999-999-999")).thenReturn(true);

        assertThrows(IsbnExistenteException.class, () -> service.update(libroUpdate));

        verify(repository, times(1)).existsByIsbn("999-9999-999-999");
        verify(repository, never()).save(any(Libro.class));
    }

    @Test
    @DisplayName("Update: Debe lanzar LibroNoEncontradoException si el ID a actualizar no existe")
    void updateLibro_NonExistentId_ThrowsException() {
        Libro libroUpdate = new Libro(idNotExisted, "T", "A", isbnNew, 1);
        when(repository.findById(idNotExisted)).thenReturn(Optional.empty());

        assertThrows(LibroNoEncontradoException.class, () -> service.update(libroUpdate));

        verify(repository, never()).save(any(Libro.class));
    }

    @Test
    @DisplayName("Debe eliminar un Libro existente por ID")
    void deleteLibro_ExistingId_DeletesSuccessfully() throws LibroNoEncontradoException {
        when(repository.findById(idExisted)).thenReturn(Optional.of(existingLibro));

        service.deleteById(idExisted);

        verify(repository, times(1)).findById(idExisted);
        verify(repository, times(1)).deleteById(idExisted);
    }

    @Test
    @DisplayName("Debe lanzar LibroNoEncontradoException al eliminar si el ID no existe")
    void deleteLibro_NonExistentId_ThrowsException() {
        when(repository.findById(idNotExisted)).thenReturn(Optional.empty());

        assertThrows(LibroNoEncontradoException.class, () -> service.deleteById(idNotExisted));

        verify(repository, times(1)).findById(idNotExisted);
        verify(repository, never()).deleteById(anyLong());
    }

}
