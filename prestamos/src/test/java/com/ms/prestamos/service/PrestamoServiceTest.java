package com.ms.prestamos.service;

import com.ms.prestamos.client.ILibroClient;
import com.ms.prestamos.client.IUsuarioClient;
import com.ms.prestamos.dto.LibroDTO;
import com.ms.prestamos.dto.UsuarioDTO;
import com.ms.prestamos.exception.ComunicacionFallidaException;
import com.ms.prestamos.exception.RecursoInvalidoException;
import com.ms.prestamos.exception.RecursoNoEncontradoException;
import com.ms.prestamos.model.Prestamo;
import com.ms.prestamos.repository.IPrestamoRepository;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de la capa de Service para Prestamo")
class PrestamoServiceTest {

    @Mock
    private IPrestamoRepository repository;

    @Mock
    private IUsuarioClient usuarioClient;

    @Mock
    private ILibroClient libroClient;

    @InjectMocks
    private PrestamoService prestamoService;

    private Prestamo prestamo;
    private UsuarioDTO usuarioDTO;
    private LibroDTO libroDTO;

    @BeforeEach
    void setUp() {
        prestamo = new Prestamo();
        prestamo.setIdUsuario(1L);
        prestamo.setIdLibro(1L);

        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId(1L);
        usuarioDTO.setActivo(true);

        libroDTO = new LibroDTO();
        libroDTO.setId(1L);
        libroDTO.setEjemplaresDisponibles(5);
    }

    @Test
    @DisplayName("Test save: registro exitoso de préstamo")
    void testSaveSuccess() throws Exception {
        when(usuarioClient.getUsuarioById(1L)).thenReturn(usuarioDTO);
        when(libroClient.getLibroById(1L)).thenReturn(libroDTO);
        when(repository.save(any(Prestamo.class))).thenReturn(prestamo);

        Prestamo result = prestamoService.save(prestamo);

        assertNotNull(result);
        assertEquals(LocalDate.now(), result.getFechaPrestamo());
        verify(repository).save(any());
        verify(libroClient).updateLibro(eq(1L), any());
    }

    @Test
    @DisplayName("Test save: falla cuando el usuario no existe")
    void testSaveUserNotFound() {
        when(usuarioClient.getUsuarioById(1L)).thenThrow(FeignException.NotFound.class);

        assertThrows(RecursoNoEncontradoException.class, () -> prestamoService.save(prestamo));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Test save: falla cuando el usuario está inactivo")
    void testSaveUserInactive() {
        usuarioDTO.setActivo(false);
        when(usuarioClient.getUsuarioById(1L)).thenReturn(usuarioDTO);

        assertThrows(RecursoInvalidoException.class, () -> prestamoService.save(prestamo));
    }

    @Test
    @DisplayName("Test save: falla cuando no hay stock disponible")
    void testSaveNoStock() {
        libroDTO.setEjemplaresDisponibles(0);
        when(usuarioClient.getUsuarioById(1L)).thenReturn(usuarioDTO);
        when(libroClient.getLibroById(1L)).thenReturn(libroDTO);

        assertThrows(RecursoInvalidoException.class, () -> prestamoService.save(prestamo));
    }

    @Test
    @DisplayName("Test save: falla por error de comunicación con microservicios")
    void testSaveCommunicationError() {
        when(usuarioClient.getUsuarioById(1L)).thenThrow(new RuntimeException());

        assertThrows(ComunicacionFallidaException.class, () -> prestamoService.save(prestamo));
    }

    @Test
    @DisplayName("Test findById: retorno exitoso de préstamo")
    void testFindByIdSuccess() throws RecursoNoEncontradoException {
        when(repository.findById(1L)).thenReturn(Optional.of(prestamo));

        Prestamo result = prestamoService.findById(1L);

        assertNotNull(result);
        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("Test returnBook: devolución exitosa")
    void testReturnBookSuccess() throws Exception {
        prestamo.setIdLibro(1L);
        prestamo.setFechaDevolucion(null);
        when(repository.findById(1L)).thenReturn(Optional.of(prestamo));
        when(libroClient.getLibroById(1L)).thenReturn(libroDTO);
        when(repository.save(any())).thenReturn(prestamo);

        Prestamo result = prestamoService.returnBook(1L);

        assertNotNull(result.getFechaDevolucion());
        assertEquals(LocalDate.now(), result.getFechaDevolucion());
        verify(libroClient).updateLibro(any(), any());
    }

    @Test
    @DisplayName("Test returnBook: falla si ya fue devuelto")
    void testReturnBookAlreadyReturned() {
        prestamo.setFechaDevolucion(LocalDate.now());
        when(repository.findById(1L)).thenReturn(Optional.of(prestamo));

        assertThrows(RecursoInvalidoException.class, () -> prestamoService.returnBook(1L));
    }

    @Test
    @DisplayName("Test getAll: obtener lista completa")
    void testGetAll() {
        when(repository.findAll()).thenReturn(List.of(prestamo));

        List<Prestamo> result = prestamoService.getAll();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }
}