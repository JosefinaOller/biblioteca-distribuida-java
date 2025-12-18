package com.ms.prestamos.controller;

import com.ms.prestamos.dto.LibroDTO;
import com.ms.prestamos.dto.PrestamoDTO;
import com.ms.prestamos.dto.UsuarioDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms.prestamos.exception.RecursoNoEncontradoException;
import com.ms.prestamos.service.IPrestamoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PrestamoController.class)
@DisplayName("Pruebas de la capa de Controller para Prestamo")
class PrestamoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IPrestamoService service;

    @Autowired
    private ObjectMapper objectMapper;

    private PrestamoDTO prestamoDTO;

    @BeforeEach
    void setUp() {
        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setId(10L);
        usuario.setNombreCompleto("Juan Perez");
        usuario.setEmail("juan@mail.com");
        usuario.setIsActivo(true);

        LibroDTO libro = new LibroDTO();
        libro.setId(20L);
        libro.setTitulo("Libro Test");
        libro.setAutor("Autor Test");
        libro.setIsbn("978-3-16-148410-0");
        libro.setEjemplaresDisponibles(5);

        prestamoDTO = new PrestamoDTO();
        prestamoDTO.setId(1L);
        prestamoDTO.setIdUsuario(10L);
        prestamoDTO.setIdLibro(20L);
        prestamoDTO.setFechaPrestamo(LocalDate.now());
        prestamoDTO.setUsuario(usuario);
        prestamoDTO.setLibro(libro);
    }

    @Test
    @DisplayName("Debe retornar 201 al crear un préstamo válido")
    void createPrestamo_Success() throws Exception {
        when(service.save(any(PrestamoDTO.class))).thenReturn(prestamoDTO);

        mockMvc.perform(post("/api/prestamos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(prestamoDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idUsuario").value(10L))
                .andExpect(jsonPath("$.usuario.nombreCompleto").value("Juan Perez"))
                .andExpect(jsonPath("$.usuario.isActivo").value(true));
    }

    @Test
    @DisplayName("Debe retornar 200 al buscar un préstamo existente por ID")
    void findPrestamoById_Success() throws Exception {
        when(service.findById(1L)).thenReturn(prestamoDTO);

        mockMvc.perform(get("/api/prestamos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.usuario.isActivo").value(true));
    }

    @Test
    @DisplayName("Debe retornar 200 al obtener todos los préstamos")
    void getPrestamos_Success() throws Exception {
        when(service.getAll()).thenReturn(List.of(prestamoDTO));

        mockMvc.perform(get("/api/prestamos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].usuario.nombreCompleto").value("Juan Perez"));
    }

    @Test
    @DisplayName("Debe retornar 404 cuando el préstamo no existe")
    void findPrestamoById_NotFound() throws Exception {
        when(service.findById(99L)).thenThrow(new RecursoNoEncontradoException("No existe"));

        mockMvc.perform(get("/api/prestamos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debe retornar 200 al devolver un libro correctamente")
    void returnBook_Success() throws Exception {
        prestamoDTO.setFechaDevolucion(LocalDate.now());
        when(service.returnBook(1L)).thenReturn(prestamoDTO);

        mockMvc.perform(post("/api/prestamos/1/devolver"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fechaDevolucion").exists());
    }
}
