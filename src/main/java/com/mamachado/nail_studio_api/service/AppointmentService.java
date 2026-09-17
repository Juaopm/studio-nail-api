package com.mamachado.nail_studio_api.service;

import com.mamachado.nail_studio_api.model.Appointment;
import com.mamachado.nail_studio_api.model.ServiceItem;
import com.mamachado.nail_studio_api.repository.AppointmentRepository;
import com.mamachado.nail_studio_api.repository.ServiceItemRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ServiceItemRepository serviceItemRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, ServiceItemRepository serviceItemRepository) {
        this.appointmentRepository = appointmentRepository;
        this.serviceItemRepository = serviceItemRepository;
    }

    public List<LocalTime> getAvailableTimes(Long serviceId, LocalDate date) {
        // Regra 1: Domingos fechados
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

            boolean isConflicting = false;

            for (Appointment existing : existingAppointments) {
                ServiceItem existingService = existing.getService();
                int existingTotalDuration = existingService.getDurationMinutes() + existingService.getBufferMinutes();
                
                LocalTime existingStart = existing.getAppointmentTime();
                LocalTime existingEnd = existingStart.plusMinutes(existingTotalDuration);

                // Correção da sobreposição (Overlap Check):
                // O horário proposto conflita se ele começa antes do existente terminar E termina depois do existente começar.
                if (candidateStart.isBefore(existingEnd) && candidateEnd.isAfter(existingStart)) {
                    isConflicting = true;
                    break;
                }
            }

            if (!isConflicting) {
                availableTimes.add(candidateStart);
            }

            // Incrementa de 30 em 30 minutos para testar o próximo slot
            currentTime = currentTime.plusMinutes(30);
        }

        return availableTimes;
    }

    public Appointment createAppointment(Long serviceId, String clientName, String clientPhone, LocalDate date, LocalTime time) {
        if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new RuntimeException("O estúdio não abre aos domingos.");
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

        return appointmentRepository.save(appointment);
    }
}