package com.mamachado.nail_studio_api.controller;

import com.mamachado.nail_studio_api.model.ServiceItem;
import com.mamachado.nail_studio_api.repository.ServiceItemRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@CrossOrigin(origins = "*") 
public class ServiceItemController {

    private final ServiceItemRepository serviceItemRepository;

    public ServiceItemController(ServiceItemRepository serviceItemRepository) {
        this.serviceItemRepository = serviceItemRepository;
    }

    // Endpoint GET para listar todos os serviços cadastrados
    // URL: http://localhost:8080/api/services
    @GetMapping
    public List<ServiceItem> getAllServices() {
        return serviceItemRepository.findAll();
    }
}