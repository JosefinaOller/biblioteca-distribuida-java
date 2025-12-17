package com.ms.prestamos.controller;

import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms.prestamos.exception.RecursoNoEncontradoException;
import com.ms.prestamos.model.Prestamo;
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

    @Test
    @DisplayName("Debe retornar 201 al crear un préstamo válido")
    void createPrestamo_Success() throws Exception {
        Prestamo prestamo = new Prestamo(1L, 10L, 20L, LocalDate.now(), null);
        when(service.save(any(Prestamo.class))).thenReturn(prestamo);

        mockMvc.perform(post("/api/prestamos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(prestamo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idUsuario").value(10L))
                .andExpect(jsonPath("$.idLibro").value(20L));
    }

    @Test
    @DisplayName("Debe retornar 200 al obtener todos los préstamos")
    void getPrestamos_Success() throws Exception {
        List<Prestamo> lista = List.of(new Prestamo(1L, 10L, 20L, LocalDate.now(), null));
        when(service.getAll()).thenReturn(lista);

        mockMvc.perform(get("/api/prestamos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("Debe retornar 200 al buscar un préstamo existente por ID")
    void findPrestamoById_Success() throws Exception {
        Prestamo prestamo = new Prestamo(1L, 10L, 20L, LocalDate.now(), null);
        when(service.findById(1L)).thenReturn(prestamo);

        mockMvc.perform(get("/api/prestamos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
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
        Prestamo prestamoDevuelto = new Prestamo(1L, 10L, 20L, LocalDate.now(), LocalDate.now());
        when(service.returnBook(1L)).thenReturn(prestamoDevuelto);

        mockMvc.perform(post("/api/prestamos/1/devolver"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fechaDevolucion").exists());
    }

}
