package com.ms.prestamos.mapper;

import com.ms.prestamos.dto.PrestamoDTO;
import com.ms.prestamos.model.Prestamo;
import org.springframework.stereotype.Component;

@Component
public class PrestamoMapper {

    public PrestamoDTO toDTO(Prestamo entity) {
        if (entity == null)
            return null;
        PrestamoDTO dto = new PrestamoDTO();
        dto.setId(entity.getId());
        dto.setIdUsuario(entity.getIdUsuario());
        dto.setIdLibro(entity.getIdLibro());
        dto.setFechaPrestamo(entity.getFechaPrestamo());
        dto.setFechaDevolucion(entity.getFechaDevolucion());
        return dto;
    }

    public Prestamo toEntity(PrestamoDTO dto) {
        if (dto == null) return null;
        Prestamo entity = new Prestamo();
        entity.setId(dto.getId());
        entity.setIdUsuario(dto.getIdUsuario());
        entity.setIdLibro(dto.getIdLibro());
        entity.setFechaPrestamo(dto.getFechaPrestamo());
        entity.setFechaDevolucion(dto.getFechaDevolucion());
        return entity;
    }
}

