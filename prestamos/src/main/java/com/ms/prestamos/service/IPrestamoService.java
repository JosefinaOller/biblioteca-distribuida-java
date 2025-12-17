package com.ms.prestamos.service;

import com.ms.prestamos.exception.*;
import com.ms.prestamos.model.Prestamo;
import java.util.List;

public interface IPrestamoService {

    Prestamo save(Prestamo prestamo) throws UsuarioNoEncontradoException, LibroNoEncontradoException, StockInsuficienteException, ComunicacionFallidaException, RecursoNoEncontradoException, RecursoInvalidoException;
    List<Prestamo> getAll();
    Prestamo findById(Long id) throws PrestamoNoEncontradoException, RecursoNoEncontradoException;
    Prestamo returnBook(Long id) throws RecursoNoEncontradoException, RecursoInvalidoException, ComunicacionFallidaException;
}
