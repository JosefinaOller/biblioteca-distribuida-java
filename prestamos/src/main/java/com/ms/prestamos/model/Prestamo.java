package com.ms.prestamos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El id de Usuario no puede estar vacío.")
    @Positive(message = "El id de Usuario debe ser mayor a cero.")
    private Long idUsuario;

    @NotNull(message = "El id de Libro no puede estar vacío.")
    @Positive(message = "El id de Libro debe ser mayor a cero.")
    private Long idLibro;

    @Column(nullable = false)
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;
}
