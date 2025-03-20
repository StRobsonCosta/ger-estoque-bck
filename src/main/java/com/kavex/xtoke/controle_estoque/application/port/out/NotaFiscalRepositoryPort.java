package com.kavex.xtoke.controle_estoque.application.port.out;

import com.kavex.xtoke.controle_estoque.domain.model.NotaFiscal;

import java.util.UUID;

public interface NotaFiscalRepositoryPort {

    NotaFiscal save(NotaFiscal notaFiscal);

    boolean existsByVendaId(UUID vendaId);
}
