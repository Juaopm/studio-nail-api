package com.mamachado.nail_studio_api.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "services")
public class ServiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes; // Ex: 120 para 2h, 60 para 1h

    @Column(name = "buffer_minutes", nullable = false)
    private Integer bufferMinutes = 30; // Nossos 30 minutos de organização

    // Construtores
    public ServiceItem() {}

    public ServiceItem(String name, BigDecimal price, Integer durationMinutes) {
        this.name = name;
        this.price = price;
        this.durationMinutes = durationMinutes;
        this.bufferMinutes = 30;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Integer getBufferMinutes() { return bufferMinutes; }
    public void setBufferMinutes(Integer bufferMinutes) { this.bufferMinutes = bufferMinutes; }
}