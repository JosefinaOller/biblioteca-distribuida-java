package com.ms.prestamos.client;

import com.ms.prestamos.dto.UsuarioDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-usuarios")
public interface IUsuarioClient {

    @GetMapping("/api/usuarios/{id}")
    UsuarioDTO getUsuarioById(@PathVariable("id") Long id);
}
