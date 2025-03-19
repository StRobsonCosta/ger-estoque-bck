package com.kavex.xtoke.controle_estoque.application.mapper;

import com.kavex.xtoke.controle_estoque.domain.model.ItemVenda;
import com.kavex.xtoke.controle_estoque.web.dto.ItemVendaDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "Spring",  uses = { ProdutoMapper.class })
public interface ItemVendaMapper {

    @Mapping(target = "produtoId", source = "produto.id")
    ItemVendaDTO toDTO(ItemVenda itemVenda);

    @Mapping(target = "produto.id", source = "produtoId")
    @Mapping(target = "subtotal", ignore = true)
    ItemVenda toEntity(ItemVendaDTO itemVendaDTO);

}
