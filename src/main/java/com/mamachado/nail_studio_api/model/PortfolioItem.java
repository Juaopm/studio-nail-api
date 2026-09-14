package com.mamachado.nail_studio_api.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "portfolio_items")
@Data
public class PortfolioItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    
    @Column(name = "image_url")
    private String imageUrl;

    private String category; 
    
    private String technique;
    
    @Column(name = "custom_date")
    private String customDate;
}