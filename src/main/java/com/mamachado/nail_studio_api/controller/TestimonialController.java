package com.mamachado.nail_studio_api.controller;

import com.mamachado.nail_studio_api.model.Testimonial;
import com.mamachado.nail_studio_api.repository.TestimonialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/testimonials")
@CrossOrigin(origins = "*")
public class TestimonialController {

    @Autowired
    private TestimonialRepository testimonialRepository;

    @GetMapping
    public List<Testimonial> getAllTestimonials() {
        return testimonialRepository.findAllByOrderByIdDesc();
    }
}