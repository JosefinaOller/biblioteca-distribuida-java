package com.ms.usuarios.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas de la entidad Usuario")
class UsuarioTest {

    @Test
    @DisplayName("Debe crear la instancia de Usuario usando el constructor con los argumentos")
    void createUsuarioWithAllArgs() {
        Long existingId = 1L;
        String existingNombre = "Josefina Oller";
        String existingEmail = "josefinaoller19@gmail.com";
        Boolean existingActivo = true;

        Usuario usuario = new Usuario(existingId, existingNombre, existingEmail, existingActivo);

        assertNotNull(usuario, "Usuario no debe ser nulo.");
        assertEquals(existingId, usuario.getId(), "El ID debe coincidir.");
        assertEquals(existingNombre, usuario.getNombreCompleto(), "El nombre completo debe coincidir.");
        assertEquals(existingEmail, usuario.getEmail(), "El email debe coincidir.");
        assertEquals(existingActivo, usuario.getIsActivo(), "El estado activo debe coincidir.");
    }

    @Test
    @DisplayName("Debe crear la instancia de Usuario usando el constructor vacío")
    void createUsuarioWithNoArgs() {
        Usuario usuario = new Usuario();
        Long existingId = 2L;
        String existingNombre = "Lionel Messi";
        String existingEmail = "messi@test.com";
        Boolean existingActivo = false;

        usuario.setId(existingId);
        usuario.setNombreCompleto(existingNombre);
        usuario.setEmail(existingEmail);
        usuario.setIsActivo(existingActivo);

        assertNotNull(usuario, "Usuario no debe ser nulo.");
        assertEquals(existingId, usuario.getId(), "El ID debe coincidir.");
        assertEquals(existingNombre, usuario.getNombreCompleto(), "El nombre completo debe coincidir.");
        assertEquals(existingEmail, usuario.getEmail(), "El email debe coincidir.");
        assertEquals(existingActivo, usuario.getIsActivo(), "El estado activo debe coincidir.");
    }

    @Test
    @DisplayName("Debe inicializar el ID como nulo usando el constructor NoArgsConstructor")
    void initializeIdAsNull() {
        Usuario usuario = new Usuario();
        assertNull(usuario.getId(), "El ID debe ser nulo al usar el constructor vacio.");
    }
}
