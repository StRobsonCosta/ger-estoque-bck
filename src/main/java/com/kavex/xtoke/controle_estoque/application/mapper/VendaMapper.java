package com.kavex.xtoke.controle_estoque.application.mapper;

import com.kavex.xtoke.controle_estoque.domain.model.Venda;
import com.kavex.xtoke.controle_estoque.web.dto.VendaDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = { ItemVendaMapper.class, ClienteMapper.class })
public interface VendaMapper {

    @Mapping(source = "cliente.id", target = "clienteId")
    @Mapping(source = "itens", target = "itens")
    VendaDTO toDTO(Venda venda);

    @Mapping(source = "clienteId", target = "cliente.id")
    @Mapping(source = "itens", target = "itens")
    Venda toEntity(VendaDTO vendaDTO);

    @Mapping(target = "id", ignore = true) // ID não deve ser alterado
    @Mapping(target = "dataVenda", ignore = true) // Data da venda geralmente não é alterada
    void updateFromDTO(VendaDTO vendaDTO, @MappingTarget Venda venda);

}
