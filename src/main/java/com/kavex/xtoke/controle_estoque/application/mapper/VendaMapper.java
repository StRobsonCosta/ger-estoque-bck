package com.kavex.xtoke.controle_estoque.application.mapper;

import com.kavex.xtoke.controle_estoque.domain.model.Venda;
import com.kavex.xtoke.controle_estoque.web.dto.VendaDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = { ItemVendaMapper.class, ClienteMapper.class })
public interface VendaMapper {

    VendaDTO toDTO(Venda venda);

    Venda toEntity(VendaDTO vendaDTO);

    @Mapping(target = "id", ignore = true) // ID não deve ser alterado
    @Mapping(target = "dataVenda", ignore = true) // Data da venda geralmente não é alterada
    void updateFromDTO(VendaDTO vendaDTO, @MappingTarget Venda venda);

}
