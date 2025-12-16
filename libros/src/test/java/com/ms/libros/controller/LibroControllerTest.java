package com.ms.libros.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms.libros.exception.IsbnExistenteException;
import com.ms.libros.exception.LibroNoEncontradoException;
import com.ms.libros.model.Libro;
import com.ms.libros.service.ILibroService;
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
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LibroController.class)
@DisplayName("Pruebas de la capa controller para Libro")
class LibroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ILibroService service;

    @Autowired
    private ObjectMapper objectMapper;

    private Libro libroRequest;
    private Libro libroResponse;
    private final Long idExisted = 1L;
    private final String isbnValid = "978-0-13-235088-4";

    @BeforeEach
    void setUp() {
        libroRequest = new Libro(null, "Clean Code", "Robert Martin", isbnValid, 5);
        libroResponse = new Libro(idExisted, "Clean Code", "Robert Martin", isbnValid, 5);
    }

    @Test
    @DisplayName("POST - Debe crear un libro y retornar la entidad creada con 201 Created")
    void createLibro_SavesSuccessfully_Returns201Created() throws Exception {
        given(service.save(any(Libro.class))).willReturn(libroResponse);

        ResultActions response = mockMvc.perform(post("/api/libros")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(libroRequest)));

        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.titulo", is("Clean Code")))
                .andExpect(jsonPath("$.isbn", is(isbnValid)));

        verify(service, times(1)).save(any(Libro.class));
    }

    @Test
    @DisplayName("POST - Debe retornar 400 BAD REQUEST si el ISBN ya existe")
    void createLibro_IsbnDuplicate_Returns400BadRequest() throws Exception {
        given(service.save(any(Libro.class)))
                .willThrow(new IsbnExistenteException("El ISBN ya existe en la base de datos."));

        mockMvc.perform(post("/api/libros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(libroRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(service, times(1)).save(any(Libro.class));
    }

    @Test
    @DisplayName("GET - Debe retornar una lista de Libros con 200 OK")
    void getLibros_ReturnsListOfLibros_Returns200OK() throws Exception {
        List<Libro> listaLibros = Arrays.asList(libroResponse, new Libro(2L, "Otro Libro", "Otro Autor", "978-9-99-999999-9", 10));
        given(service.getAll()).willReturn(listaLibros);

        ResultActions response = mockMvc.perform(get("/api/libros")
                .contentType(MediaType.APPLICATION_JSON));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].titulo", is("Clean Code")));

        verify(service, times(1)).getAll();
    }

    @Test
    @DisplayName("GET - Debe buscar el libro por ID y retornarlo con 200 OK")
    void findLibroById_ExistingId_Returns200OK() throws Exception {
        given(service.findById(idExisted)).willReturn(libroResponse);

        ResultActions response = mockMvc.perform(get("/api/libros/{id}", idExisted));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo", is("Clean Code")));

        verify(service, times(1)).findById(idExisted);
    }

    @Test
    @DisplayName("GET - Debe retornar 404 NOT FOUND si el libro no existe")
    void findLibroById_NonExistentId_Returns404NotFound() throws Exception {
        Long nonExistentId = 99L;
        given(service.findById(nonExistentId))
                .willThrow(new LibroNoEncontradoException("Libro no encontrado con ID: " + nonExistentId));

        mockMvc.perform(get("/api/libros/{id}", nonExistentId))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("PUT - Debe actualizar el libro y retornarlo con 200 OK")
    void updateLibro_UpdatesSuccessfully_Returns200OK() throws Exception {
        libroRequest.setId(idExisted);
        given(service.update(any(Libro.class))).willReturn(libroResponse);

        ResultActions response = mockMvc.perform(put("/api/libros/{id}", idExisted)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(libroRequest)));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo", is("Clean Code")));

        verify(service, times(1)).update(any(Libro.class));
    }

    @Test
    @DisplayName("PUT - Debe retornar 404 NOT FOUND si intentamos actualizar un ID inexistente")
    void updateLibro_NonExistentId_Returns404NotFound() throws Exception {
        Long nonExistentId = 99L;
        libroRequest.setId(nonExistentId);

        given(service.update(any(Libro.class)))
                .willThrow(new LibroNoEncontradoException("No se puede actualizar, ID no encontrado."));

        mockMvc.perform(put("/api/libros/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(libroRequest)))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).update(any(Libro.class));
    }

    @Test
    @DisplayName("PUT - Debe retornar 400 BAD REQUEST si actualizamos el ISBN a uno ya existente")
    void updateLibro_DuplicateIsbn_Returns400BadRequest() throws Exception {
        libroRequest.setId(idExisted);

        given(service.update(any(Libro.class)))
                .willThrow(new IsbnExistenteException("El nuevo ISBN ya pertenece a otro libro."));

        mockMvc.perform(put("/api/libros/{id}", idExisted)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(libroRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(service, times(1)).update(any(Libro.class));
    }

    @Test
    @DisplayName("DELETE - Debe eliminar el libro por ID y retornar 204 No Content")
    void deleteLibroById_DeletesSuccessfully_Returns204NoContent() throws Exception {
        willDoNothing().given(service).deleteById(idExisted);

        mockMvc.perform(delete("/api/libros/{id}", idExisted))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(service, times(1)).deleteById(idExisted);
    }

    @Test
    @DisplayName("DELETE - Debe retornar 404 NOT FOUND si el libro a eliminar no existe")
    void deleteLibroById_NonExistentId_Returns404NotFound() throws Exception {
        Long nonExistentId = 99L;
        willThrow(new LibroNoEncontradoException("No se puede eliminar, ID no encontrado."))
                .given(service).deleteById(nonExistentId);

        mockMvc.perform(delete("/api/libros/{id}", nonExistentId))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).deleteById(nonExistentId);
    }
}