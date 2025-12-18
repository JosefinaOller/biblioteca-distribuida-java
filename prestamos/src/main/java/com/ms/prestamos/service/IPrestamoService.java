package com.ms.prestamos.service;

import com.ms.prestamos.dto.PrestamoDTO;
import com.ms.prestamos.exception.*;
import java.util.List;

public interface IPrestamoService {

    PrestamoDTO save(PrestamoDTO prestamoDTO) throws ComunicacionFallidaException, RecursoNoEncontradoException, RecursoInvalidoException;
    List<PrestamoDTO> getAll();
    PrestamoDTO findById(Long id) throws RecursoNoEncontradoException;
    PrestamoDTO returnBook(Long id) throws RecursoNoEncontradoException, RecursoInvalidoException, ComunicacionFallidaException;
}
