package com.mamachado.nail_studio_api.repository;

import com.mamachado.nail_studio_api.model.PortfolioItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioRepository extends JpaRepository<PortfolioItem, Long> {
}