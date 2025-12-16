package com.ms.libros.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.ISBN;

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
    @Size(min = 10, max = 20, message = "El ISBN debe tener entre 10 y 20 caracteres.")
    private String isbn;

    @Min(value = 0, message = "El stock de ejemplares no puede ser negativo.")
    private int ejemplaresDisponibles;
}
