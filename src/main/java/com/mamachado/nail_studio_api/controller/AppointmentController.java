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
    @GetMapping("/available-times")
    public ResponseEntity<List<LocalTime>> getAvailableTimes(
            @RequestParam Long serviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        List<LocalTime> availableTimes = appointmentService.getAvailableTimes(serviceId, date);
        return ResponseEntity.ok(availableTimes);
    }

    // DTO para receber os dados do corpo da requisição do React
    public static class AppointmentRequest {
        public Long serviceId;
        public String clientName;
        public String clientPhone;
        public LocalDate date;
        public LocalTime time;
    }

    // Endpoint POST para criar o agendamento (retorna o objeto salvo contendo o confirmationToken)
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
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoints de Ação Rápida via Token (Acessados diretamente pelos links do WhatsApp)
    @GetMapping("/token/{token}/confirmar")
    @ResponseBody
    public String confirmAppointment(@PathVariable String token) {
        try {
            appointmentService.updateAppointmentStatus(token, "CONFIRMADO");
            return htmlResponse("Agendamento Confirmado! ✨", "O status foi atualizado para CONFIRMADO com sucesso no sistema.", "green");
        } catch (Exception e) {
            return htmlResponse("Erro ao Processar", e.getMessage(), "red");
        }
    }

    @GetMapping("/token/{token}/recusar")
    @ResponseBody
    public String cancelAppointment(@PathVariable String token) {
        try {
            appointmentService.updateAppointmentStatus(token, "RECUSADO");
            return htmlResponse("Agendamento Recusado", "O status foi atualizado para RECUSADO.", "amber");
        } catch (Exception e) {
            return htmlResponse("Erro ao Processar", e.getMessage(), "red");
        }
    }

    @GetMapping("/token/{token}/reagendar")
    @ResponseBody
    public String rescheduleAppointment(@PathVariable String token) {
        try {
            appointmentService.updateAppointmentStatus(token, "REAGENDAR");
            return htmlResponse("Solicitação de Reagendamento", "O agendamento foi marcado para REAGENDAR. Entre em contato com a cliente.", "blue");
        } catch (Exception e) {
            return htmlResponse("Erro ao Processar", e.getMessage(), "red");
        }
    }

    // Método auxiliar para renderizar a página HTML de resposta leve e elegante
    private String htmlResponse(String title, String message, String themeColor) {
        String colorClass = "bg-green-500";
        if (themeColor.equals("red")) colorClass = "bg-red-500";
        if (themeColor.equals("amber")) colorClass = "bg-amber-500";
        if (themeColor.equals("blue")) colorClass = "bg-blue-500";

        return "<html lang='pt-BR'>" +
                "<head><meta charset='UTF-8'><title>" + title + "</title>" +
                "<script src='https://cdn.tailwindcss.com'></script></head>" +
                "<body class='bg-zinc-950 text-white flex items-center justify-center min-h-screen'>" +
                "<div class='bg-zinc-900 border border-zinc-800 p-8 rounded-2xl shadow-xl text-center max-w-md w-full space-y-4'>" +
                "<div class='w-12 h-12 " + colorClass + " rounded-full flex items-center justify-center mx-auto text-white font-bold text-xl'>✓</div>" +
                "<h1 class='text-2xl font-light tracking-wide'>" + title + "</h1>" +
                "<p class='text-zinc-400 text-sm leading-relaxed'>" + message + "</p>" +
                "<p class='text-xs text-zinc-600 pt-4'>Pode fechar esta aba com segurança.</p>" +
                "</div></body></html>";
    }
}