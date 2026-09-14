package com.mamachado.nail_studio_api.controller;

import com.mamachado.nail_studio_api.model.PortfolioItem;
import com.mamachado.nail_studio_api.repository.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolio")
@CrossOrigin(origins = "*") // Permite que o front-end consuma a API sem problemas de CORS
public class PortfolioController {

    @Autowired
    private PortfolioRepository portfolioRepository;

    @GetMapping
    public List<PortfolioItem> getAllItems() {
        return portfolioRepository.findAll();
    }

    @PostMapping
    public PortfolioItem createItem(@RequestBody PortfolioItem item) {
        return portfolioRepository.save(item);
    }
}