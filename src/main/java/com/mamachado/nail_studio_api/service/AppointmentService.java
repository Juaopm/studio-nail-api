package com.mamachado.nail_studio_api.service;

import com.mamachado.nail_studio_api.model.Appointment;
import com.mamachado.nail_studio_api.model.ServiceItem;
import com.mamachado.nail_studio_api.repository.AppointmentRepository;
import com.mamachado.nail_studio_api.repository.ServiceItemRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ServiceItemRepository serviceItemRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, 
                              ServiceItemRepository serviceItemRepository) {
        this.appointmentRepository = appointmentRepository;
        this.serviceItemRepository = serviceItemRepository;
    }

    public List<LocalTime> getAvailableTimes(Long serviceId, LocalDate date) {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        if (date.isBefore(today)) {
            throw new RuntimeException("Não é permitido agendar horários para datas passadas.");
        }

        if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return new ArrayList<>();
        }

        ServiceItem service = serviceItemRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

        List<Appointment> existingAppointments = appointmentRepository.findByAppointmentDate(date);

        List<LocalTime> availableTimes = new ArrayList<>();
        LocalTime openingTime = LocalTime.of(8, 0);
        LocalTime closingTime = LocalTime.of(19, 30);

        int totalServiceDurationMinutes = service.getDurationMinutes() + service.getBufferMinutes();
        LocalTime currentTime = openingTime;

        while (currentTime.plusMinutes(service.getDurationMinutes()).compareTo(closingTime) <= 0) {
            LocalTime candidateStart = currentTime;
            LocalTime candidateEnd = currentTime.plusMinutes(totalServiceDurationMinutes);

            LocalDateTime candidateDateTime = LocalDateTime.of(date, candidateStart);
            if (candidateDateTime.isBefore(now.plusHours(24))) {
                currentTime = currentTime.plusMinutes(30);
                continue;
            }

            boolean isConflicting = false;

            for (Appointment existing : existingAppointments) {
                ServiceItem existingService = existing.getService();
                int existingTotalDuration = existingService.getDurationMinutes() + existingService.getBufferMinutes();
                
                LocalTime existingStart = existing.getAppointmentTime();
                LocalTime existingEnd = existingStart.plusMinutes(existingTotalDuration);

                if (candidateStart.isBefore(existingEnd) && candidateEnd.isAfter(existingStart)) {
                    isConflicting = true;
                    break;
                }
            }

            if (!isConflicting) {
                availableTimes.add(candidateStart);
            }

            currentTime = currentTime.plusMinutes(30);
        }

        return availableTimes;
    }

    public Appointment createAppointment(Long serviceId, String clientName, String clientPhone, LocalDate date, LocalTime time) {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        if (date.isBefore(today)) {
            throw new RuntimeException("Não é permitido realizar agendamentos em datas passadas.");
        }

        if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new RuntimeException("O estúdio não abre aos domingos.");
        }

        LocalDateTime candidateDateTime = LocalDateTime.of(date, time);
        if (candidateDateTime.isBefore(now.plusHours(24))) {
            throw new RuntimeException("O agendamento deve ser feito com no mínimo 24 horas de antecedência.");
        }

        ServiceItem service = serviceItemRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

        List<Appointment> existingAppointments = appointmentRepository.findByAppointmentDate(date);
        int totalServiceDurationMinutes = service.getDurationMinutes() + service.getBufferMinutes();
        LocalTime candidateEnd = time.plusMinutes(totalServiceDurationMinutes);

        for (Appointment existing : existingAppointments) {
            ServiceItem existingService = existing.getService();
            int existingTotalDuration = existingService.getDurationMinutes() + existingService.getBufferMinutes();
            
            LocalTime existingStart = existing.getAppointmentTime();
            LocalTime existingEnd = existingStart.plusMinutes(existingTotalDuration);

            if (time.isBefore(existingEnd) && candidateEnd.isAfter(existingStart)) {
                throw new RuntimeException("Este horário não está mais disponível. Escolha outro slot.");
            }
        }

        Appointment appointment = new Appointment();
        appointment.setService(service);
        appointment.setClientName(clientName);
        appointment.setClientPhone(clientPhone);
        appointment.setAppointmentDate(date);
        appointment.setAppointmentTime(time);
        appointment.setStatus("PENDENTE");
        appointment.setConfirmationToken(UUID.randomUUID().toString()); // <--- Gera o token único v4

        return appointmentRepository.save(appointment);
    }

    public Appointment updateAppointmentStatus(String token, String newStatus) {
        Appointment appointment = appointmentRepository.findByConfirmationToken(token)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado para o token informado."));
        
        appointment.setStatus(newStatus);
        return appointmentRepository.save(appointment);
    }
}