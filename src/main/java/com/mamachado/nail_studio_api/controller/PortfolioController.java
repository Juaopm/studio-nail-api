package com.mamachado.nail_studio_api.controller;

import com.mamachado.nail_studio_api.model.PortfolioItem;
import com.mamachado.nail_studio_api.repository.PortfolioRepository;
import com.mamachado.nail_studio_api.service.PortfolioImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolio")
@CrossOrigin(origins = "*")
public class PortfolioController {

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private PortfolioImportService importService;

    @GetMapping
    public List<PortfolioItem> getAllPortfolioItems() {
        return portfolioRepository.findAllByOrderByIdDesc();
    }

    @PostMapping
    public PortfolioItem createItem(@RequestBody PortfolioItem item) {
        return portfolioRepository.save(item);
    }

    @PostMapping("/import-folder")
    public ResponseEntity<String> importPhotos(@RequestParam String path) {
        try {
            int importedCount = importService.importLocalFolder(path);
            return ResponseEntity.ok("Sucesso! " + importedCount + " fotos importadas para o banco.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao importar: " + e.getMessage());
        }
    }
}