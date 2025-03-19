package com.kavex.xtoke.controle_estoque.application.mapper;

import com.kavex.xtoke.controle_estoque.domain.model.Cliente;
import com.kavex.xtoke.controle_estoque.web.dto.ClienteDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    ClienteDTO toDTO(Cliente cliente);
    Cliente toEntity(ClienteDTO clienteDTO);

    @Mapping(target = "id", ignore = true)
    void updateFromDTO(ClienteDTO clienteDTO, @MappingTarget Cliente cliente);
}
