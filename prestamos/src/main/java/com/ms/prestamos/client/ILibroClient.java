package com.ms.prestamos.client;

import com.ms.prestamos.dto.LibroDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-libros")
public interface ILibroClient {

    @GetMapping("/api/libros/{id}")
    LibroDTO getLibroById(@PathVariable("id") Long id);

    @PutMapping("/api/libros/{id}")
    LibroDTO updateLibro(@PathVariable("id") Long id, @RequestBody LibroDTO libroDTO);

}
