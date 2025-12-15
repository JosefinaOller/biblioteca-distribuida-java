package com.ms.libros.controller;

import com.ms.libros.exception.IsbnExistenteException;
import com.ms.libros.exception.LibroNoEncontradoException;
import com.ms.libros.model.Libro;
import com.ms.libros.service.ILibroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/libros")
@Tag(name = "Libros", description = "Operaciones CRUD para la gestión de libros.")
public class LibroController {

    private final ILibroService service;

    @Autowired
    public LibroController(ILibroService service) {
        this.service = service;
    }

    @Operation(
            summary = "Crear un nuevo libro",
            description = "Registra un nuevo libro en el catálogo. Retorna 409 si el ISBN ya está registrado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Libro creado exitosamente.",
                    content = @Content(schema = @Schema(implementation = Libro.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos (ej: ISBN vacío, stock negativo)."),
            @ApiResponse(responseCode = "409", description = "Conflicto: Ya existe un libro con ese ISBN.")
    })

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Libro createLibro(@Valid @RequestBody Libro libro) throws IsbnExistenteException {
        return service.save(libro);
    }

    @Operation(
            summary = "Obtener todos los libros",
            description = "Retorna una lista de todos los libros registrados en el sistema."
    )
    @ApiResponse(responseCode = "200", description = "Lista de libros obtenida exitosamente.",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Libro.class))))

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Libro> getLibros() {
        return service.getAll();
    }

    @Operation(
            summary = "Buscar libro por ID",
            description = "Retorna un libro específico por su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Libro encontrado exitosamente.",
                    content = @Content(schema = @Schema(implementation = Libro.class))),
            @ApiResponse(responseCode = "404", description = "Libro no encontrado (ID inexistente).")
    })

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public Libro findLibroById(@PathVariable Long id) throws LibroNoEncontradoException {
        return service.findById(id);
    }

    @Operation(
            summary = "Actualizar un libro",
            description = "Actualiza los datos de un libro existente. Retorna 409 si el nuevo ISBN ya pertenece a otro libro."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Libro actualizado exitosamente.",
                    content = @Content(schema = @Schema(implementation = Libro.class))),
            @ApiResponse(responseCode = "404", description = "Libro no encontrado (ID inexistente)."),
            @ApiResponse(responseCode = "400", description = "Datos inválidos."),
            @ApiResponse(responseCode = "409", description = "Conflicto: El nuevo ISBN ya está en uso por otro libro.")
    })

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public Libro updateLibro(@PathVariable Long id, @Valid @RequestBody Libro libro) throws LibroNoEncontradoException, IsbnExistenteException {
        libro.setId(id);
        return service.update(libro);
    }

    @Operation(
            summary = "Eliminar libro por ID",
            description = "Elimina un libro del sistema por su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Libro eliminado exitosamente (No Content)."),
            @ApiResponse(responseCode = "404", description = "Libro no encontrado con el ID proporcionado.")
    })

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLibroById(@PathVariable Long id) throws LibroNoEncontradoException {
        service.deleteById(id);
    }
}
