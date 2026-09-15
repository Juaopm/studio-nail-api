package com.mamachado.nail_studio_api.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "testimonials")
@Data
public class Testimonial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Column(name = "alt_text")
    private String altText;
}