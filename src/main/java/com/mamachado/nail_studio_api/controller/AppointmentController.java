package com.mamachado.nail_studio_api.controller;

import com.mamachado.nail_studio_api.model.Appointment;
import com.mamachado.nail_studio_api.service.AppointmentService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    // Endpoint GET para buscar os horários disponíveis
    // Exemplo de chamada: /api/appointments/available-times?serviceId=1&date=2026-06-15
    @GetMapping("/available-times")
    public ResponseEntity<List<LocalTime>> getAvailableTimes(
            @RequestParam Long serviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        List<LocalTime> availableTimes = appointmentService.getAvailableTimes(serviceId, date);
        return ResponseEntity.ok(availableTimes);
    }

    // DTO (Objeto de Transferência) simples para receber os dados do corpo da requisição do React
    public static class AppointmentRequest {
        public Long serviceId;
        public String clientName;
        public String clientPhone;
        public LocalDate date;
        public LocalTime time;
    }

    // Endpoint POST para criar o agendamento (fica como PENDENTE aguardando a validação humana)
    // URL: http://localhost:8080/api/appointments
    @PostMapping
    public ResponseEntity<?> createAppointment(@RequestBody AppointmentRequest request) {
        try {
            Appointment createdAppointment = appointmentService.createAppointment(
                    request.serviceId,
                    request.clientName,
                    request.clientPhone,
                    request.date,
                    request.time
            );
            return ResponseEntity.ok(createdAppointment);
        } catch (RuntimeException e) {
            // Se houver conflito ou regra violada, devolve erro 400 com a mensagem explicativa
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}