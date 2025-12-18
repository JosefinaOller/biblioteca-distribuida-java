package com.ms.prestamos.service;

import com.ms.prestamos.client.ILibroClient;
import com.ms.prestamos.client.IUsuarioClient;
import com.ms.prestamos.dto.LibroDTO;
import com.ms.prestamos.dto.PrestamoDTO;
import com.ms.prestamos.dto.UsuarioDTO;
import com.ms.prestamos.exception.ComunicacionFallidaException;
import com.ms.prestamos.exception.RecursoInvalidoException;
import com.ms.prestamos.exception.RecursoNoEncontradoException;
import com.ms.prestamos.mapper.PrestamoMapper;
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

    @Mock
    private PrestamoMapper mapper;

    @InjectMocks
    private PrestamoService service;

    private Prestamo entidad;
    private PrestamoDTO prestamoDTO;
    private UsuarioDTO usuarioDTO;
    private LibroDTO libroDTO;

    @BeforeEach
    void setUp() {
        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId(1L);
        usuarioDTO.setNombreCompleto("Juan Perez");
        usuarioDTO.setEmail("juan@mail.com");
        usuarioDTO.setIsActivo(true);

        libroDTO = new LibroDTO();
        libroDTO.setId(10L);
        libroDTO.setTitulo("Libro Test");
        libroDTO.setAutor("Autor Test");
        libroDTO.setIsbn("123456789");
        libroDTO.setEjemplaresDisponibles(5);

        entidad = new Prestamo();
        entidad.setId(1L);
        entidad.setIdUsuario(1L);
        entidad.setIdLibro(10L);
        entidad.setFechaPrestamo(LocalDate.now());

        prestamoDTO = new PrestamoDTO();
        prestamoDTO.setId(1L);
        prestamoDTO.setIdUsuario(1L);
        prestamoDTO.setIdLibro(10L);
    }

    @Test
    @DisplayName("Test save: registro exitoso de préstamo")
    void testSaveSuccess() throws Exception {
        when(usuarioClient.getUsuarioById(1L)).thenReturn(usuarioDTO);
        when(libroClient.getLibroById(10L)).thenReturn(libroDTO);
        when(mapper.toEntity(any(PrestamoDTO.class))).thenReturn(entidad);
        when(repository.save(any(Prestamo.class))).thenReturn(entidad);
        when(mapper.toDTO(any(Prestamo.class))).thenReturn(prestamoDTO);

        PrestamoDTO result = service.save(prestamoDTO);

        assertNotNull(result);
        verify(repository).save(any());
        verify(libroClient).updateLibro(eq(10L), any());
    }

    @Test
    @DisplayName("Test save: falla cuando el usuario no existe")
    void testSaveUserNotFound() {
        when(usuarioClient.getUsuarioById(1L)).thenThrow(FeignException.NotFound.class);

        assertThrows(RecursoNoEncontradoException.class, () -> service.save(prestamoDTO));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Test save: falla cuando el usuario está inactivo")
    void testSaveUserInactive() {
        usuarioDTO.setIsActivo(false);
        when(usuarioClient.getUsuarioById(1L)).thenReturn(usuarioDTO);

        assertThrows(RecursoInvalidoException.class, () -> service.save(prestamoDTO));
    }

    @Test
    @DisplayName("Test save: falla cuando no hay stock disponible")
    void testSaveNoStock() {
        libroDTO.setEjemplaresDisponibles(0);
        when(usuarioClient.getUsuarioById(1L)).thenReturn(usuarioDTO);
        when(libroClient.getLibroById(10L)).thenReturn(libroDTO);

        assertThrows(RecursoInvalidoException.class, () -> service.save(prestamoDTO));
    }

    @Test
    @DisplayName("Test save: falla por error de comunicación")
    void testSaveCommunicationError() {
        when(usuarioClient.getUsuarioById(1L)).thenThrow(new RuntimeException());

        assertThrows(ComunicacionFallidaException.class, () -> service.save(prestamoDTO));
    }

    @Test
    @DisplayName("Test findById: retorno exitoso")
    void testFindByIdSuccess() throws RecursoNoEncontradoException {
        when(repository.findById(1L)).thenReturn(Optional.of(entidad));
        when(mapper.toDTO(entidad)).thenReturn(prestamoDTO);
        when(usuarioClient.getUsuarioById(1L)).thenReturn(usuarioDTO);
        when(libroClient.getLibroById(10L)).thenReturn(libroDTO);

        PrestamoDTO result = service.findById(1L);

        assertNotNull(result);
        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("Test returnBook: devolución exitosa")
    void testReturnBookSuccess() throws Exception {
        entidad.setFechaDevolucion(null);
        when(repository.findById(1L)).thenReturn(Optional.of(entidad));
        when(libroClient.getLibroById(10L)).thenReturn(libroDTO);
        when(repository.save(any())).thenReturn(entidad);
        when(mapper.toDTO(any())).thenReturn(prestamoDTO);

        PrestamoDTO result = service.returnBook(1L);

        assertNotNull(result);
        verify(libroClient).updateLibro(any(), any());
        verify(repository).save(any());
    }

    @Test
    @DisplayName("Test getAll: obtener lista completa")
    void testGetAll() {
        when(repository.findAll()).thenReturn(List.of(entidad));
        when(mapper.toDTO(entidad)).thenReturn(prestamoDTO);
        when(usuarioClient.getUsuarioById(anyLong())).thenReturn(usuarioDTO);
        when(libroClient.getLibroById(anyLong())).thenReturn(libroDTO);

        List<PrestamoDTO> result = service.getAll();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }
}