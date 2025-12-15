package com.ms.libros.service;

import com.ms.libros.exception.IsbnExistenteException;
import com.ms.libros.exception.LibroNoEncontradoException;
import com.ms.libros.model.Libro;

import java.util.List;

public interface ILibroService {

    Libro save(Libro libro) throws IsbnExistenteException;
    List<Libro> getAll();
    Libro findById(Long id) throws LibroNoEncontradoException;
    Libro update(Libro libro) throws LibroNoEncontradoException, IsbnExistenteException;
    void deleteById(Long id) throws LibroNoEncontradoException;
}
