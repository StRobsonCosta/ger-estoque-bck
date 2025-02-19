package com.kavex.xtoke.controle_estoque.application.mapper;

import com.kavex.xtoke.controle_estoque.domain.model.Venda;
import com.kavex.xtoke.controle_estoque.web.dto.VendaDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface VendaMapper {

    VendaMapper INSTANCE = Mappers.getMapper(VendaMapper.class);

    VendaDTO toDTO(Venda venda);
    Venda toEntity(VendaDTO vendaDTO);

}
