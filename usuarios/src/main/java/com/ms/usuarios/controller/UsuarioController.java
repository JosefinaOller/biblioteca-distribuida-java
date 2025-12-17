package com.ms.usuarios.controller;

import com.ms.usuarios.exception.EmailExistenteException;
import com.ms.usuarios.exception.UsuarioNoEncontradoException;
import com.ms.usuarios.model.Usuario;
import com.ms.usuarios.service.IUsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Operaciones para la gestión de usuarios.")
@RequiredArgsConstructor
public class UsuarioController {

    private final IUsuarioService service;

    @Operation(
            summary = "Registrar un nuevo usuario",
            description = "Crea un registro de usuario. El usuario nace con estado 'activo' por defecto. Valida que el email no esté duplicado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente.",
                    content = @Content(schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos (ej: email mal formado)."),
            @ApiResponse(responseCode = "409", description = "Conflicto: El email ya se encuentra registrado.")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario createUsuario(@Valid @RequestBody Usuario usuario) throws EmailExistenteException {
        return service.save(usuario);
    }

    @Operation(
            summary = "Obtener todos los usuarios",
            description = "Retorna una lista completa de todos los usuarios registrados en el sistema."
    )
    @ApiResponse(responseCode = "200", description = "Lista obten de usuarios obtenida exitosamente.",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Usuario.class))))
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Usuario> getAllUsuarios() {
        return service.getAll();
    }

    @Operation(
            summary = "Buscar usuario por ID",
            description = "Retorna los detalles de un usuario específico basado en su ID autogenerado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente.",
                    content = @Content(schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado (ID inexistente).")
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Usuario getUsuarioById(@PathVariable Long id) throws UsuarioNoEncontradoException {
        return service.findById(id);
    }

    @Operation(
            summary = "Desactivar un usuario",
            description = "Realiza una baja lógica del usuario cambiando su estado 'isActivo' a false."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario desactivado correctamente.",
                    content = @Content(schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "404", description = "No se puede desactivar: Usuario no encontrado.")
    })
    @PatchMapping("/{id}/desactivar")
    @ResponseStatus(HttpStatus.OK)
    public Usuario desactivateUsuario(@PathVariable Long id) throws UsuarioNoEncontradoException {
        return service.desactivateUsuario(id);
    }

    @Operation(
            summary = "Eliminar un usuario",
            description = "Elimina físicamente el registro del usuario del sistema por su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente (No Content)."),
            @ApiResponse(responseCode = "404", description = "No se puede eliminar: Usuario no encontrado.")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUsuario(@PathVariable Long id) throws UsuarioNoEncontradoException {
        service.deleteById(id);
    }
}
