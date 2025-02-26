package com.kavex.xtoke.controle_estoque.application.port.out;

import com.kavex.xtoke.controle_estoque.domain.model.NotaFiscal;

public interface NotaFiscalRepositoryPort {

    NotaFiscal save(NotaFiscal notaFiscal);
}
