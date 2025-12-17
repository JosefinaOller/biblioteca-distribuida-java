package com.ms.usuarios.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío.")
    @Size(min = 1, max = 80, message = "El título debe tener entre 1 y 80 caracteres.")
    private String nombreCompleto;

    @NotBlank(message = "El email no puede estar vacío.")
    @Email(message = "Debe proporcionar un formato de email válido.")
    @Column(unique = true, nullable = false)
    private String email;

    @NotNull(message = "El estado activo es obligatorio.")
    private boolean isActivo;
}
