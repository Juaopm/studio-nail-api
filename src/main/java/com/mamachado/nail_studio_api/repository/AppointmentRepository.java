package com.mamachado.nail_studio_api.repository;

import com.mamachado.nail_studio_api.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Busca todos os agendamentos de uma data específica para sabermos quais horários estão ocupados
    List<Appointment> findByAppointmentDate(LocalDate appointmentDate);

    // Busca o agendamento correspondente ao token único gerado para confirmação via WhatsApp
    Optional<Appointment> findByConfirmationToken(String confirmationToken);
    
}