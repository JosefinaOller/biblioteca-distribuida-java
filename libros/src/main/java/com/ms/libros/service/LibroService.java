package com.ms.libros.service;

import com.ms.libros.exception.IsbnExistenteException;
import com.ms.libros.exception.LibroNoEncontradoException;
import com.ms.libros.model.Libro;
import com.ms.libros.repository.ILibroRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LibroService implements ILibroService {

    private final ILibroRepository repository;
    private static final Logger log = LoggerFactory.getLogger(LibroService.class);

    @Autowired
    public LibroService(ILibroRepository repository) {
        this.repository = repository;
    }

    @Override
    public Libro save(Libro libro) throws IsbnExistenteException {
        if(repository.existsByIsbn(libro.getIsbn())) {
            log.warn("Libro no puede ser guardado");
            throw new IsbnExistenteException("El ISBN " + libro.getIsbn() + " ya está registrado en otro libro. ");
        }
        log.info("Libro guardando...");
        return repository.save(libro);
    }

    @Override
    public List<Libro> getAll() {
        return repository.findAll();
    }

    @Override
    public Libro findById(Long id) throws LibroNoEncontradoException {
        return repository.findById(id).orElseThrow(()->{
            log.warn("No se encontró el libro con ID: {}", id);
            return new LibroNoEncontradoException("El libro con ID " + id + " no existe");
        });
    }

    @Override
    public Libro update(Libro newLibro) throws LibroNoEncontradoException, IsbnExistenteException {
        Libro updateLibro = findById(newLibro.getId());

        if(!updateLibro.getIsbn().equals(newLibro.getIsbn()) && repository.existsByIsbn(newLibro.getIsbn())) {
            throw new IsbnExistenteException("El ISBN " + newLibro.getIsbn() + " ya está registrado en otro libro. ");
        }
        updateLibro.setTitulo(newLibro.getTitulo());
        updateLibro.setAutor(newLibro.getAutor());
        updateLibro.setIsbn(newLibro.getIsbn());
        updateLibro.setEjemplaresDisponibles(newLibro.getEjemplaresDisponibles());
        log.info("Actualizando el libro con ID: {}", updateLibro.getId());
        return repository.save(updateLibro);
    }

    @Override
    public void deleteById(Long id) throws LibroNoEncontradoException {
        findById(id);
        log.info("Eliminado el libro con ID: {}", id);
        repository.deleteById(id);
    }
}
