package com.mamachado.nail_studio_api.service;

import com.mamachado.nail_studio_api.model.PortfolioItem;
import com.mamachado.nail_studio_api.repository.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class PortfolioImportService {

    @Autowired
    private PortfolioRepository portfolioRepository;

    public int importLocalFolder(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            throw new RuntimeException("Diretório não encontrado: " + folderPath);
        }

        File[] files = folder.listFiles((dir, name) -> {
            String lower = name.toLowerCase();
            return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") || lower.endsWith(".webp");
        });

        if (files == null || files.length == 0) {
            return 0;
        }

        List<PortfolioItem> itemsToSave = new ArrayList<>();
        // Referência do seu projeto Supabase para montar a URL pública do Storage
        String supabaseUrlPrefix = "https://jstxwuitdwirwqxqebmc.supabase.co/storage/v1/object/public/portfolio-images/";

        for (File file : files) {
            String fileName = file.getName();
            // Formata o título baseado no nome do arquivo (ex: "fibra-de-vidro-1.jpg" vira "Fibra De Vidro 1")
            String title = formatTitle(fileName);
            
            // Monta a URL pública que o Supabase vai servir após os arquivos estarem no bucket
            String imageUrl = supabaseUrlPrefix + fileName;

            PortfolioItem item = new PortfolioItem();
            item.setTitle(title);
            item.setImageUrl(imageUrl);
            item.setCategory("Trabalhos Exclusivos"); // Categoria padrão ou dinâmica

            itemsToSave.add(item);
        }

        portfolioRepository.saveAll(itemsToSave);
        return itemsToSave.size();
    }

    private String formatTitle(String fileName) {
        // Remove a extensão
        int dotIndex = fileName.lastIndexOf('.');
        String nameWithoutExt = (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
        // Substitui hífens e underscores por espaços
        String cleaned = nameWithoutExt.replace("-", " ").replace("_", " ");
        // Capitaliza as palavras básicas
        return cleaned.toUpperCase();
    }
}