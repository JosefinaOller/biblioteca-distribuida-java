package com.ms.prestamos.service;

import com.ms.prestamos.client.ILibroClient;
import com.ms.prestamos.client.IUsuarioClient;
import com.ms.prestamos.dto.LibroDTO;
import com.ms.prestamos.dto.PrestamoDTO;
import com.ms.prestamos.dto.UsuarioDTO;
import com.ms.prestamos.exception.*;
import com.ms.prestamos.mapper.PrestamoMapper;
import com.ms.prestamos.model.Prestamo;
import com.ms.prestamos.repository.IPrestamoRepository;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrestamoService implements IPrestamoService{

    private static final Logger log = LoggerFactory.getLogger(PrestamoService.class);
    private final IPrestamoRepository repository;
    private final IUsuarioClient usuarioClient;
    private final ILibroClient libroClient;
    private final PrestamoMapper mapper;


    @Override
    @Transactional
    public PrestamoDTO save(PrestamoDTO prestamoDTO) throws ComunicacionFallidaException, RecursoNoEncontradoException, RecursoInvalidoException {

        UsuarioDTO usuario = validateUserExists(prestamoDTO.getIdUsuario());
        LibroDTO libro = findAndValidateBookStock(prestamoDTO.getIdLibro());

        Prestamo entidad = mapper.toEntity(prestamoDTO);
        setLoanDate(entidad);
        entidad.setFechaDevolucion(null);

        Prestamo entidadGuardada = repository.save(entidad);
        log.info("Préstamo registrado correctamente con ID {}", entidadGuardada.getId());

        updateRemoteStock(libro);

        PrestamoDTO respuesta = mapper.toDTO(entidadGuardada);
        respuesta.setUsuario(usuario);
        respuesta.setLibro(libro);

        return respuesta;
    }

    private void updateRemoteStock(LibroDTO libro) throws ComunicacionFallidaException {
        try {
            libro.setEjemplaresDisponibles(libro.getEjemplaresDisponibles() - 1);
            libroClient.updateLibro(libro.getId(), libro);
            log.info("El stock de libro prestado se actualizó. ");
        } catch (Exception e) {
            log.error("Error al actualizar stock. Realizando Rollback del préstamo.", e);
            throw new ComunicacionFallidaException("Error al actualizar el stock del libro. Se cancela el préstamo.");
        }
    }

    private void setLoanDate(Prestamo prestamo) {
        if (prestamo.getFechaPrestamo()== null) {
            prestamo.setFechaPrestamo(LocalDate.now());
        }
    }

    private LibroDTO findAndValidateBookStock(Long idLibro) throws RecursoNoEncontradoException, ComunicacionFallidaException, RecursoInvalidoException {
        LibroDTO libro;
        try {
            libro = libroClient.getLibroById(idLibro);
        } catch (FeignException.NotFound e) {
            log.warn("El libro con ID {} no existe", idLibro);
            throw new RecursoNoEncontradoException("El libro con ID  " + idLibro + " no existe");
        } catch (Exception e) {
            log.error("Error al conectar con MS Libros", e);
            throw new ComunicacionFallidaException("No se pudo comunicar con el microservicio de Libros.");
        }

        if (libro.getEjemplaresDisponibles() <= 0) {
            log.warn("El libro no tiene stock");
            throw new RecursoInvalidoException("No hay stock disponible.");
        }
        log.info("Libro validado y con stock disponible.");
        return libro;
    }

    private UsuarioDTO validateUserExists(Long idUsuario) throws RecursoNoEncontradoException, ComunicacionFallidaException, RecursoInvalidoException {
        UsuarioDTO usuario;
        try {
            usuario = usuarioClient.getUsuarioById(idUsuario);
        } catch (FeignException.NotFound e) {
            log.warn("El usuario con ID {} no existe", idUsuario);
            throw new RecursoNoEncontradoException("El usuario con ID " + idUsuario + " no existe");
        } catch (Exception e) {
            log.error("Error al conectar con MS Usuarios", e);
            throw new ComunicacionFallidaException("No se pudo comunicar con el microservicio de Usuarios.");
        }
        if (!Boolean.TRUE.equals(usuario.getIsActivo())){
            log.warn("El usuario no es activo.");
            throw new RecursoInvalidoException("El usuario no es activo.");
        }
        log.info("Usuario validado correctamente.");
        return usuario;
    }

    @Override
    public List<PrestamoDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(prestamo -> {
                    PrestamoDTO dto = mapper.toDTO(prestamo);
                    return enrichPrestamo(dto);
                })
                .toList();
    }

    @Override
    public PrestamoDTO findById(Long id) throws RecursoNoEncontradoException {
        Prestamo entity = findEntityById(id);
        PrestamoDTO dto = mapper.toDTO(entity);
        return enrichPrestamo(dto);
    }

    @Override
    @Transactional
    public PrestamoDTO returnBook(Long id) throws RecursoNoEncontradoException, RecursoInvalidoException, ComunicacionFallidaException {
        Prestamo prestamo = findEntityById(id);

        validateLoanNotReturned(prestamo.getFechaDevolucion());

        prestamo.setFechaDevolucion(LocalDate.now());
        repository.save(prestamo);
        log.info("El prestamo con ID {} fue actualizado correctamente.", prestamo.getId());

        updateReturnedBookStock(prestamo.getIdLibro());

        PrestamoDTO dto = mapper.toDTO(prestamo);
        return enrichPrestamo(dto);
    }

    private void updateReturnedBookStock(Long idLibro) throws ComunicacionFallidaException {
        try {
            LibroDTO libro = libroClient.getLibroById(idLibro);
            libro.setEjemplaresDisponibles(libro.getEjemplaresDisponibles()+1);
            libroClient.updateLibro(libro.getId(), libro);
            log.info("El stock de libro devuelto fue actualizado correctamente.");
        } catch (Exception e) {
            log.error("Error crítico al actualizar stock en devolución.", e);
            throw new ComunicacionFallidaException("No se pudo actualizar el stock de libro");
        }
    }

    private void validateLoanNotReturned(LocalDate fechaDevolucion) throws RecursoInvalidoException {
        if (fechaDevolucion != null) {
            log.error("El prestamo ya fue devuelto!");
            throw new RecursoInvalidoException("El prestamo ya fue devuelto.");
        }
    }

    private Prestamo findEntityById(Long id) throws RecursoNoEncontradoException {
        return repository.findById(id).orElseThrow(() -> {
            log.warn("No se encontró el prestamo con ID: {}", id);
            return new RecursoNoEncontradoException("El prestamo con ID " + id + " no existe");
        });
    }

    private PrestamoDTO enrichPrestamo(PrestamoDTO dto) {
        try {
            dto.setUsuario(usuarioClient.getUsuarioById(dto.getIdUsuario()));
            dto.setLibro(libroClient.getLibroById(dto.getIdLibro()));
        } catch (Exception e) {
            log.error("Error al enriquecer el préstamo {}: {}", dto.getId(), e.getMessage());
        }
        return dto;
    }
}
