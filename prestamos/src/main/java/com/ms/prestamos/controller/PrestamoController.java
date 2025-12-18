package com.ms.prestamos.controller;

import com.ms.prestamos.dto.PrestamoDTO;
import com.ms.prestamos.exception.ComunicacionFallidaException;
import com.ms.prestamos.exception.RecursoInvalidoException;
import com.ms.prestamos.exception.RecursoNoEncontradoException;
import com.ms.prestamos.service.IPrestamoService;
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
@RequestMapping("/api/prestamos")
@Tag(name = "Préstamos", description = "Operaciones para la gestión de préstamos de libros.")
public class PrestamoController {

    private final IPrestamoService service;

    @Autowired
    public PrestamoController(IPrestamoService service) {
        this.service = service;
    }

    @Operation(
            summary = "Registrar un nuevo préstamo",
            description = "Crea un registro de préstamo. Valida que el usuario esté activo y el libro tenga stock disponible."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Préstamo registrado exitosamente.",
                    content = @Content(schema = @Schema(implementation = PrestamoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos."),
            @ApiResponse(responseCode = "404", description = "Usuario o Libro no encontrado."),
            @ApiResponse(responseCode = "409", description = "Conflicto: Usuario inactivo o Libro sin stock."),
            @ApiResponse(responseCode = "503", description = "Error de comunicación con microservicios externos.")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PrestamoDTO createPrestamo(@Valid @RequestBody PrestamoDTO prestamo) throws ComunicacionFallidaException, RecursoInvalidoException, RecursoNoEncontradoException {
        return service.save(prestamo);
    }

    @Operation(
            summary = "Obtener historial de préstamos",
            description = "Retorna una lista de todos los préstamos registrados."
    )
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente.",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = PrestamoDTO.class))))
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PrestamoDTO> getPrestamos() {
        return service.getAll();
    }

    @Operation(
            summary = "Buscar préstamo por ID",
            description = "Retorna los detalles de un préstamo específico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Préstamo encontrado.",
                    content = @Content(schema = @Schema(implementation = PrestamoDTO.class))),
            @ApiResponse(responseCode = "404", description = "ID de préstamo inexistente.")
    })


    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public PrestamoDTO findPrestamoById(@PathVariable Long id) throws RecursoNoEncontradoException {
        return service.findById(id);
    }

    @Operation(
            summary = "Devolver un libro",
            description = "Registra la fecha de devolución y restaura el stock del libro."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Devolución procesada correctamente.",
                    content = @Content(schema = @Schema(implementation = PrestamoDTO.class))),
            @ApiResponse(responseCode = "404", description = "El préstamo no existe."),
            @ApiResponse(responseCode = "409", description = "El préstamo ya fue devuelto previamente."),
            @ApiResponse(responseCode = "503", description = "No se pudo actualizar el stock en el microservicio de libros.")
    })

    @PostMapping("{id}/devolver")
    @ResponseStatus(HttpStatus.OK)
    public PrestamoDTO returnBook(@PathVariable Long id) throws RecursoInvalidoException, ComunicacionFallidaException, RecursoNoEncontradoException {
        return service.returnBook(id);
    }
}
