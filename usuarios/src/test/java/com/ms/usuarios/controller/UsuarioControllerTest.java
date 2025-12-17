package com.ms.usuarios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms.usuarios.exception.EmailExistenteException;
import com.ms.usuarios.exception.UsuarioNoEncontradoException;
import com.ms.usuarios.model.Usuario;
import com.ms.usuarios.service.IUsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import java.util.Arrays;
import java.util.List;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@DisplayName("Pruebas de la capa controller para Usuario")
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IUsuarioService service;

    @Autowired
    private ObjectMapper objectMapper;

    private Usuario usuarioRequest;
    private Usuario usuarioResponse;
    private final Long idExisted = 1L;
    private final String emailValid = "josefinaoller19@gmail.com";

    @BeforeEach
    void setUp() {
        usuarioRequest = new Usuario(null, "Josefina Oller", emailValid, null);
        usuarioResponse = new Usuario(idExisted, "Josefina Oller", emailValid, true);
    }

    @Test
    @DisplayName("POST - Debe crear un usuario y retornar la entidad con 201 Created")
    void createUsuario_SavesSuccessfully_Returns201Created() throws Exception {
        given(service.save(any(Usuario.class))).willReturn(usuarioResponse);

        ResultActions response = mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioRequest)));

        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombreCompleto", is("Josefina Oller")))
                .andExpect(jsonPath("$.isActivo", is(true)));

        verify(service, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("POST - Debe retornar 400 BAD REQUEST si el email ya existe")
    void createUsuario_EmailDuplicate_Returns409Conflict() throws Exception {
        given(service.save(any(Usuario.class)))
                .willThrow(new EmailExistenteException("El email ya se encuentra registrado."));

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(service, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("GET - Debe retornar una lista de Usuarios con 200 OK")
    void getAllUsuarios_ReturnsListOfUsuarios_Returns200OK() throws Exception {
        List<Usuario> listaUsuarios = Arrays.asList(usuarioResponse, new Usuario(2L, "Lionel Messi", "messi@test.com", true));
        given(service.getAll()).willReturn(listaUsuarios);

        ResultActions response = mockMvc.perform(get("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombreCompleto", is("Josefina Oller")));

        verify(service, times(1)).getAll();
    }

    @Test
    @DisplayName("GET - Debe buscar el usuario por ID y retornarlo con 200 OK")
    void getUsuarioById_ExistingId_Returns200OK() throws Exception {
        given(service.findById(idExisted)).willReturn(usuarioResponse);

        ResultActions response = mockMvc.perform(get("/api/usuarios/{id}", idExisted));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreCompleto", is("Josefina Oller")));

        verify(service, times(1)).findById(idExisted);
    }

    @Test
    @DisplayName("GET - Debe retornar 404 NOT FOUND si el usuario no existe")
    void getUsuarioById_NonExistentId_Returns404NotFound() throws Exception {
        Long nonExistentId = 99L;
        given(service.findById(nonExistentId))
                .willThrow(new UsuarioNoEncontradoException("Usuario no encontrado con ID: " + nonExistentId));

        mockMvc.perform(get("/api/usuarios/{id}", nonExistentId))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("PATCH - Debe desactivar el usuario y retornarlo con 200 OK")
    void deactivateUsuario_UpdatesSuccessfully_Returns200OK() throws Exception {
        Usuario usuarioInactivo = new Usuario(idExisted, "Josefina Oller", emailValid, false);
        given(service.desactivateUsuario(idExisted)).willReturn(usuarioInactivo);

        ResultActions response = mockMvc.perform(patch("/api/usuarios/{id}/desactivar", idExisted));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActivo", is(false)));

        verify(service, times(1)).desactivateUsuario(idExisted);
    }

    @Test
    @DisplayName("DELETE - Debe eliminar el usuario por ID y retornar 204 No Content")
    void deleteUsuario_DeletesSuccessfully_Returns204NoContent() throws Exception {
        willDoNothing().given(service).deleteById(idExisted);

        mockMvc.perform(delete("/api/usuarios/{id}", idExisted))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(service, times(1)).deleteById(idExisted);
    }

    @Test
    @DisplayName("DELETE - Debe retornar 404 NOT FOUND si el usuario a eliminar no existe")
    void deleteUsuario_NonExistentId_Returns404NotFound() throws Exception {
        Long nonExistentId = 99L;
        willThrow(new UsuarioNoEncontradoException("No se puede eliminar, ID no encontrado."))
                .given(service).deleteById(nonExistentId);

        mockMvc.perform(delete("/api/usuarios/{id}", nonExistentId))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).deleteById(nonExistentId);
    }
}