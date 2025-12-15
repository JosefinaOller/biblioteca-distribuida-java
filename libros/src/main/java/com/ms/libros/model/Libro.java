package com.ms.libros.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título no puede estar vacío.")
    @Size(min = 1, max = 80, message = "El título debe tener entre 1 y 80 caracteres.")
    private String titulo;

    @NotBlank(message = "El autor no puede estar vacío.")
    @Size(min = 2, max = 80, message = "El autor debe tener entre 2 y 80 caracteres.")
    private String autor;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "El ISBN no puede estar vacío.")
    @Size(min = 13, max = 13, message = "El ISBN debe tener 13 dígitos.")
    @Pattern(regexp = "^\\d+$", message = "El ISBN debe contener solo números.")
    private String isbn;

    @Min(value = 0, message = "El stock de ejemplares no puede ser negativo.")
    private int ejemplaresDisponibles;
}
