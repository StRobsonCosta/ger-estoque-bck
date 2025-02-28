package com.kavex.xtoke.controle_estoque.application.mapper;

import com.kavex.xtoke.controle_estoque.domain.model.Cliente;
import com.kavex.xtoke.controle_estoque.domain.model.Usuario;
import com.kavex.xtoke.controle_estoque.web.dto.ClienteDTO;
import com.kavex.xtoke.controle_estoque.web.dto.UsuarioDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    UsuarioMapper INSTANCE = Mappers.getMapper(UsuarioMapper .class);

    UsuarioDTO toDTO(Usuario usuario);

    Usuario toEntity(UsuarioDTO usuarioDTO);

}
