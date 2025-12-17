package com.ms.usuarios.service;

import com.ms.usuarios.exception.EmailExistenteException;
import com.ms.usuarios.exception.UsuarioNoEncontradoException;
import com.ms.usuarios.model.Usuario;
import com.ms.usuarios.repository.IUsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService implements IUsuarioService {

    private final IUsuarioRepository repository;
    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    @Override
    @Transactional
    public Usuario save(Usuario usuario) throws EmailExistenteException {

        if (repository.existsByEmail(usuario.getEmail())) {
            log.warn("El email ya existe, no se puede generar usuario.");
            throw new EmailExistenteException("El email ya existe");
        }
        usuario.setActivo(true);
        log.info("Usuario guardado correctamente.");
        return repository.save(usuario);
    }

    @Override
    public List<Usuario> getAll() {
        return repository.findAll();
    }

    @Override
    public Usuario findById(Long id) throws UsuarioNoEncontradoException {
        return repository.findById(id).orElseThrow(()->{
            log.warn("No se encontró el usuario con ID: {}", id);
            return new UsuarioNoEncontradoException("El usuario con ID " + id + " no existe");
        });
    }

    @Override
    @Transactional
    public Usuario desactivateUsuario(Long id) throws UsuarioNoEncontradoException {
        Usuario usuario = findById(id);
        usuario.setActivo(false);
        log.info("Usuario desactivado correctamente.");
        return repository.save(usuario);
    }

    @Override
    @Transactional
    public void deleteById(Long id) throws UsuarioNoEncontradoException {
        findById(id);
        log.info("Usuario eliminado correctamente.");
        repository.deleteById(id);
    }
}
