package com.mamachado.nail_studio_api.repository;

import com.mamachado.nail_studio_api.model.Testimonial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestimonialRepository extends JpaRepository<Testimonial, Long> {
    List<Testimonial> findAllByOrderByIdDesc();
}