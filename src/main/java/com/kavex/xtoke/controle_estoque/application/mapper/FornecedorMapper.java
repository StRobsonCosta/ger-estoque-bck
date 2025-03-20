package com.kavex.xtoke.controle_estoque.application.mapper;

import com.kavex.xtoke.controle_estoque.domain.model.Fornecedor;
import com.kavex.xtoke.controle_estoque.web.dto.FornecedorDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface FornecedorMapper {

    FornecedorDTO toDTO(Fornecedor fornecedor);
    Fornecedor toEntity(FornecedorDTO fornecedorDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataCadastro", ignore = true)
    void updateFromDTO(FornecedorDTO fornecedorDTO, @MappingTarget Fornecedor fornecedor);
}
