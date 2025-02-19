package com.kavex.xtoke.controle_estoque.application.mapper;

import com.kavex.xtoke.controle_estoque.domain.model.ItemVenda;
import com.kavex.xtoke.controle_estoque.web.dto.ItemVendaDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "Spring")
public interface ItemVendaMapper {

    ItemVendaMapper INSTANCE = Mappers.getMapper(ItemVendaMapper.class);

    ItemVendaDTO toDTO(ItemVenda itemVenda);
    ItemVenda toEntity(ItemVendaDTO itemVendaDTO);

}
