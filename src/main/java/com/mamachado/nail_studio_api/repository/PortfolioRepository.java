package com.mamachado.nail_studio_api.repository;

import com.mamachado.nail_studio_api.model.PortfolioItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortfolioRepository extends JpaRepository<PortfolioItem, Long> {
    
    // Retorna todos os itens ordenados do ID mais alto (último inserido) para o mais baixo
    List<PortfolioItem> findAllByOrderByIdDesc();
}