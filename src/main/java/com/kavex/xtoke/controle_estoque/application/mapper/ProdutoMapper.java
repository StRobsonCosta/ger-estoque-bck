package com.kavex.xtoke.controle_estoque.application.mapper;

import com.kavex.xtoke.controle_estoque.domain.model.Fornecedor;
import com.kavex.xtoke.controle_estoque.domain.model.Produto;
import com.kavex.xtoke.controle_estoque.web.dto.FornecedorDTO;
import com.kavex.xtoke.controle_estoque.web.dto.ProdutoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring" , uses = { Fornecedor.class })
public interface ProdutoMapper {

    @Mapping(source = "fornecedor.id", target = "fornecedorId")
    ProdutoDTO toDTO(Produto produto);

    @Mapping(source = "estoqueMinimo", target = "estoqueMinimo")  // Adicionando mapeamento explícito
    @Mapping(source = "unidadeMedida", target = "unidadeMedida")
    @Mapping(source = "fornecedorId", target = "fornecedor.id")
    Produto toEntity(ProdutoDTO produtoDTO);

    @Mapping(target = "id", ignore = true) // ID não deve ser alterado
    @Mapping(target = "dataCadastro", ignore = true) // Data da venda geralmente não é alterada
    void updateFromDTO(ProdutoDTO produtoDTO, @MappingTarget Produto produto);

}
