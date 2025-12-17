package com.ms.usuarios.service;

import com.ms.usuarios.exception.EmailExistenteException;
import com.ms.usuarios.exception.UsuarioNoEncontradoException;
import com.ms.usuarios.model.Usuario;
import com.ms.usuarios.repository.IUsuarioRepository;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de la capa Service para Usuario")
class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService service;

    @Mock
    private IUsuarioRepository repository;

    private Usuario existingUsuario;
    private final Long idExisted = 1L;
    private final Long idNotExisted = 99L;
    private final String emailExisted = "josefinaoller19@gmail.com";
    private final String emailNew = "nuevo.usuario@test.com";

    @BeforeEach
    void setUp() {
        existingUsuario = new Usuario(idExisted, "Josefina Oller", emailExisted, true);
    }

    @Test
    @DisplayName("Debe guardar un Usuario si el email no existe")
    void saveUsuario_WithNewEmail_SavesSuccessfully() throws EmailExistenteException {
        Usuario newUsuario = new Usuario(null, "Usuario Nuevo", emailNew, null);
        Usuario savedUsuario = new Usuario(2L, "Usuario Nuevo", emailNew, true);

        when(repository.existsByEmail(newUsuario.getEmail())).thenReturn(false);
        when(repository.save(any(Usuario.class))).thenReturn(savedUsuario);

        Usuario result = service.save(newUsuario);

        assertNotNull(result, "El usuario guardado no debe ser nulo.");
        assertEquals(emailNew, result.getEmail(), "El email debe coincidir.");
        assertTrue(result.getIsActivo(), "El usuario debe guardarse con estado activo.");
        assertEquals(2L, result.getId(), "El ID debe ser el generado.");

        verify(repository, times(1)).existsByEmail(newUsuario.getEmail());
        verify(repository, times(1)).save(newUsuario);
    }

    @Test
    @DisplayName("Debe lanzar EmailExistenteException al guardar si el email ya está registrado")
    void saveUsuario_DuplicateEmail_ThrowsException() {
        Usuario duplicateUsuario = new Usuario(null, "Intento Duplicado", emailExisted, null);

        when(repository.existsByEmail(emailExisted)).thenReturn(true);

        assertThrows(EmailExistenteException.class, () -> service.save(duplicateUsuario),
                "Debe lanzar EmailExistenteException cuando el email ya existe.");

        verify(repository, times(1)).existsByEmail(emailExisted);
        verify(repository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe retornar un Usuario existente por ID")
    void findUsuarioById_ExistingId_ReturnsUsuario() throws UsuarioNoEncontradoException {
        when(repository.findById(idExisted)).thenReturn(Optional.of(existingUsuario));

        Usuario result = service.findById(idExisted);

        assertNotNull(result, "El usuario retornado no debe ser nulo.");
        assertEquals(idExisted, result.getId(), "El ID debe coincidir.");
        assertEquals("Josefina Oller", result.getNombreCompleto(), "El nombre debe coincidir.");

        verify(repository, times(1)).findById(idExisted);
    }

    @Test
    @DisplayName("Debe lanzar UsuarioNoEncontradoException si el ID no existe")
    void findUsuarioById_NonExistentId_ThrowsException() {
        when(repository.findById(idNotExisted)).thenReturn(Optional.empty());

        assertThrows(UsuarioNoEncontradoException.class, () -> service.findById(idNotExisted),
                "Debe lanzar excepción si el usuario no se encuentra.");

        verify(repository, times(1)).findById(idNotExisted);
    }

    @Test
    @DisplayName("Debe retornar una lista de Usuarios")
    void getAll_ReturnsListOfUsuarios() {
        Usuario usuario2 = new Usuario(2L, "Lionel Messi", "messi@test.com", true);
        List<Usuario> listMock = Arrays.asList(existingUsuario, usuario2);

        when(repository.findAll()).thenReturn(listMock);

        List<Usuario> result = service.getAll();

        assertNotNull(result, "La lista no debe ser nula.");
        assertEquals(2, result.size(), "Debe retornar 2 usuarios.");
        assertEquals(emailExisted, result.get(0).getEmail());

        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Desactivate: Debe cambiar isActivo a false correctamente")
    void deactivateUsuario_ExistingId_DeactivatesSuccessfully() throws UsuarioNoEncontradoException {
        when(repository.findById(idExisted)).thenReturn(Optional.of(existingUsuario));
        when(repository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario result = service.desactivateUsuario(idExisted);

        assertNotNull(result);
        assertFalse(result.getIsActivo(), "El estado isActivo debe ser false tras la desactivación.");

        verify(repository, times(1)).findById(idExisted);
        verify(repository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe eliminar un Usuario existente por ID")
    void deleteUsuario_ExistingId_DeletesSuccessfully() throws UsuarioNoEncontradoException {
        when(repository.findById(idExisted)).thenReturn(Optional.of(existingUsuario));

        service.deleteById(idExisted);

        verify(repository, times(1)).findById(idExisted);
        verify(repository, times(1)).deleteById(idExisted);
    }

    @Test
    @DisplayName("Debe lanzar UsuarioNoEncontradoException al eliminar si el ID no existe")
    void deleteUsuario_NonExistentId_ThrowsException() {
        when(repository.findById(idNotExisted)).thenReturn(Optional.empty());

        assertThrows(UsuarioNoEncontradoException.class, () -> service.deleteById(idNotExisted));

        verify(repository, times(1)).findById(idNotExisted);
        verify(repository, never()).deleteById(anyLong());
    }
}