package com.kavex.xtoke.controle_estoque.application.mapper;

import com.kavex.xtoke.controle_estoque.domain.model.Produto;
import com.kavex.xtoke.controle_estoque.web.dto.ProdutoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProdutoMapper {

    ProdutoMapper INSTANCE = Mappers.getMapper(ProdutoMapper.class);

    @Mapping(source = "fornecedor.id", target = "fornecedorId")
    ProdutoDTO toDTO(Produto produto);

    @Mapping(source = "fornecedorId", target = "fornecedor.id")
    Produto toEntity(ProdutoDTO produtoDTO);

}
