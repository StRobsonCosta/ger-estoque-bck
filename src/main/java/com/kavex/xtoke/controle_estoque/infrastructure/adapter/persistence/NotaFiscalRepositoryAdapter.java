package com.kavex.xtoke.controle_estoque.infrastructure.adapter.persistence;

import com.kavex.xtoke.controle_estoque.application.port.out.NotaFiscalRepositoryPort;
import com.kavex.xtoke.controle_estoque.domain.model.NotaFiscal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotaFiscalRepositoryAdapter extends JpaRepository<NotaFiscal, UUID>, NotaFiscalRepositoryPort {
}
