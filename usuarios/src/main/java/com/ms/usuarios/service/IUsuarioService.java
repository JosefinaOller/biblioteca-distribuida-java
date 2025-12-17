package com.ms.usuarios.service;

import com.ms.usuarios.exception.EmailExistenteException;
import com.ms.usuarios.exception.UsuarioNoEncontradoException;
import com.ms.usuarios.model.Usuario;
import java.util.List;

public interface IUsuarioService {

    Usuario save(Usuario usuario) throws EmailExistenteException;
    List<Usuario> getAll();
    Usuario findById(Long id) throws UsuarioNoEncontradoException;
    Usuario desactivateUsuario(Long id) throws UsuarioNoEncontradoException;
    void deleteById(Long id) throws UsuarioNoEncontradoException;
}
