package com.mamachado.nail_studio_api.repository;

import com.mamachado.nail_studio_api.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Busca todos os agendamentos de uma data específica para sabermos quais horários estão ocupados
    List<Appointment> findByAppointmentDate(LocalDate appointmentDate);
    
}