package com.ms.prestamos.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrestamoDTO {

    private Long id;

    @NotNull(message = "El id de Usuario no puede estar vacío.")
    @Positive(message = "El id de Usuario debe ser mayor a cero.")
    private Long idUsuario;

    @NotNull(message = "El id de Libro no puede estar vacío.")
    @Positive(message = "El id de Libro debe ser mayor a cero.")
    private Long idLibro;

    private UsuarioDTO usuario;
    private LibroDTO libro;

    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;
}
