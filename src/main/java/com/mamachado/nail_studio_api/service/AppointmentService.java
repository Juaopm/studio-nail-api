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
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ServiceItemRepository serviceItemRepository;

    // Horário de funcionamento fixo do estúdio
    private static final LocalTime OPENING_TIME = LocalTime.of(8, 0);
    private static final LocalTime CLOSING_TIME = LocalTime.of(19, 30);

    public AppointmentService(AppointmentRepository appointmentRepository, ServiceItemRepository serviceItemRepository) {
        this.appointmentRepository = appointmentRepository;
        this.serviceItemRepository = serviceItemRepository;
    }

    /**
     * 1. Retorna todos os horários livres para um determinado serviço em uma data específica
     */
    public List<LocalTime> getAvailableTimes(Long serviceId, LocalDate date) {
        // Validação: Domingos não abrem
        if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return new ArrayList<>(); // Retorna lista vazia
        }

        // Validação: Antecedência mínima de 24 horas
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime requestedDateTime = LocalDateTime.of(date, OPENING_TIME);
        if (requestedDateTime.isBefore(now.plusHours(24))) {
            // Se a data escolhida for em menos de 24 horas, bloqueia
            if (date.isEqual(LocalDate.now())) {
                return new ArrayList<>();
            }
        }

        // Buscar o serviço para saber a duração exata + buffer
        ServiceItem service = serviceItemRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

        int totalDurationMinutes = service.getDurationMinutes() + service.getBufferMinutes();

        // Gerar todos os slots possíveis de horários no dia (ex: de 30 em 30 min)
        List<LocalTime> allPossibleSlots = generateDailySlots(totalDurationMinutes);

        // Buscar agendamentos já existentes para essa data no banco
        List<Appointment> existingAppointments = appointmentRepository.findByAppointmentDate(date);

        // Filtrar removendo os horários que já estão ocupados ou que colidem
        return allPossibleSlots.stream().filter(slot -> {
            LocalTime slotEndTime = slot.plusMinutes(totalDurationMinutes);

            // O slot não pode ultrapassar o horário de fechamento do estúdio
            if (slotEndTime.isAfter(CLOSING_TIME)) {
                return false;
            }

            // Verificar colisão com agendamentos já salvos
            for (Appointment existing : existingAppointments) {
                // Pega a duração do serviço já agendado
                int existingTotalDuration = existing.getService().getDurationMinutes() + existing.getService().getBufferMinutes();
                LocalTime existingStart = existing.getAppointmentTime();
                LocalTime existingEnd = existingStart.plusMinutes(existingTotalDuration);

                // Lógica de sobreposição de horários (overlap check)
                // Se o slot proposto começa antes do agendamento existente terminar E termina depois do agendamento existente começar
                if (slot.isBefore(existingEnd) && slotEndTime.isAfter(existingStart)) {
                    return false; // Conflito! Horário ocupado.
                }
            }

            return true; // Horário livre
        }).collect(Collectors.toList());
    }

    /**
     * 2. Cria um novo agendamento após validar se o horário ainda está disponível
     */
    public Appointment createAppointment(Long serviceId, String clientName, String clientPhone, LocalDate date, LocalTime time) {
        // Valida se o horário escolhido está realmente na lista de disponíveis
        List<LocalTime> availableTimes = getAvailableTimes(serviceId, date);
        if (!availableTimes.contains(time)) {
            throw new RuntimeException("O horário selecionado não está mais disponível ou viola as regras de agendamento.");
        }

        ServiceItem service = serviceItemRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

        Appointment appointment = new Appointment(clientName, clientPhone, service, date, time);
        return appointmentRepository.save(appointment);
    }

    /**
     * Método auxiliar para gerar todos os horários base do dia com base no intervalo de expediente
     */
    private List<LocalTime> generateDailySlots(int totalDurationMinutes) {
        List<LocalTime> slots = new ArrayList<>();
        LocalTime currentTime = OPENING_TIME;

        // Geramos horários incrementando de 30 em 30 minutos ao longo do dia
        while (currentTime.isBefore(CLOSING_TIME)) {
            slots.add(currentTime);
            currentTime = currentTime.plusMinutes(30);
        }

        return slots;
    }
}