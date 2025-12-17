package com.ms.usuarios.repository;

import com.ms.usuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUsuarioRepository extends JpaRepository<Usuario,  Long> {
    boolean existsByEmail(String email);
}
