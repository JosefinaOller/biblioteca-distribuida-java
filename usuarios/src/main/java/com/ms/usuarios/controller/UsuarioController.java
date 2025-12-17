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
@RequiredArgsConstructor
public class UsuarioController {

    private final IUsuarioService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario createUsuario(@Valid @RequestBody Usuario usuario) throws EmailExistenteException {
        return service.save(usuario);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Usuario> getAllUsuarios() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Usuario getUsuarioById(@PathVariable Long id) throws UsuarioNoEncontradoException {
        return service.findById(id);
    }


    @PatchMapping("/{id}/desactivar")
    @ResponseStatus(HttpStatus.OK)
    public Usuario desactivateUsuario(@PathVariable Long id) throws UsuarioNoEncontradoException {
        return service.desactivateUsuario(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUsuario(@PathVariable Long id) throws UsuarioNoEncontradoException {
        service.deleteById(id);
    }
}
