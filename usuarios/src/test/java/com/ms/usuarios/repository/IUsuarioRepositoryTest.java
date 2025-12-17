package com.ms.usuarios.repository;

import com.ms.usuarios.model.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("Pruebas de la capa de Repository para Usuario")
class IUsuarioRepositoryTest {

    @Autowired
    private IUsuarioRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Debe guardar un Usuario y generar su ID")
    void saveUsuario() {
        Usuario newUsuario = new Usuario(null, "Josefina Oller", "josefina@test.com", true);

        Usuario savedUsuario = repository.save(newUsuario);

        assertNotNull(savedUsuario.getId(), "El ID debe ser generado por JPA.");
        assertEquals("Josefina Oller", savedUsuario.getNombreCompleto());
        assertEquals("josefina@test.com", savedUsuario.getEmail());
    }

    @Test
    @DisplayName("Debe encontrar un Usuario por ID")
    void findUsuarioById() {
        Usuario usuario = new Usuario(null, "Lionel Messi", "messi@test.com", true);
        entityManager.persistAndFlush(usuario);
        Long idFound = usuario.getId();

        Optional<Usuario> result = repository.findById(idFound);

        assertTrue(result.isPresent(), "Se debe encontrar el usuario.");
        assertEquals("Lionel Messi", result.get().getNombreCompleto());
        assertEquals("messi@test.com", result.get().getEmail());
    }

    @Test
    @DisplayName("Debe retornar vacío si el ID no existe")
    void returnEmptyWhenIdNotFound() {
        Long idNotFound = 99L;

        Optional<Usuario> result = repository.findById(idNotFound);

        assertTrue(result.isEmpty(), "Debe retornar vacío para un ID que no existe.");
    }

    @Test
    @DisplayName("Debe eliminar un Usuario por ID y confirmar su ausencia")
    void deleteUsuarioById() {
        Usuario usuarioToDelete = new Usuario(null, "Usuario Borrar", "borrar@test.com", true);
        entityManager.persistAndFlush(usuarioToDelete);
        Long idToDelete = usuarioToDelete.getId();

        repository.deleteById(idToDelete);

        Optional<Usuario> result = repository.findById(idToDelete);
        assertTrue(result.isEmpty(), "El usuario debe haber sido eliminado.");
    }

    @Test
    @DisplayName("existsByEmail: Retorna TRUE si el email existe en la BD")
    void existsByEmail_ExistingEmail_ReturnsTrue() {
        String emailExisted = "josefinaoller19@gmail.com";
        Usuario usuario = new Usuario(null, "Josefina", emailExisted, true);
        entityManager.persistAndFlush(usuario);

        boolean isEmailPresent = repository.existsByEmail(emailExisted);

        assertTrue(isEmailPresent, "Debe retornar true si el email existe.");
    }

    @Test
    @DisplayName("existsByEmail: Retorna FALSE si el email no existe en la BD")
    void existsByEmail_NotExistingEmail_ReturnsFalse() {
        String emailNotExisted = "noexiste@test.com";

        boolean isEmailPresent = repository.existsByEmail(emailNotExisted);

        assertFalse(isEmailPresent, "Debe retornar false si el email no existe.");
    }
}
