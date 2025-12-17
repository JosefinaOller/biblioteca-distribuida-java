package com.ms.prestamos.service;

import com.ms.prestamos.exception.*;
import com.ms.prestamos.model.Prestamo;
import java.util.List;

public interface IPrestamoService {

    Prestamo save(Prestamo prestamo) throws ComunicacionFallidaException, RecursoNoEncontradoException, RecursoInvalidoException;
    List<Prestamo> getAll();
    Prestamo findById(Long id) throws RecursoNoEncontradoException;
    Prestamo returnBook(Long id) throws RecursoNoEncontradoException, RecursoInvalidoException, ComunicacionFallidaException;
}
