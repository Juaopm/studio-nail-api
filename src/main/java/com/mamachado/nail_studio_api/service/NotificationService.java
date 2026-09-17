package com.mamachado.nail_studio_api.service;

import com.mamachado.nail_studio_api.model.Appointment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationService {

    // URL do Webhook do n8n (você vai substituir pela URL real que o n8n te der)
    private static final String N8N_WEBHOOK_URL = "http://localhost:5678/webhook/novo-agendamento";

    public void notifyProfessional(Appointment appointment) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> payload = new HashMap<>();
            payload.put("appointmentId", appointment.getId());
            payload.put("clientName", appointment.getClientName());
            payload.put("clientPhone", appointment.getClientPhone());
            payload.put("serviceName", appointment.getService().getName());
            payload.put("date", appointment.getAppointmentDate().toString());
            payload.put("time", appointment.getAppointmentTime().toString());

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            restTemplate.postForEntity(N8N_WEBHOOK_URL, request, String.class);
            
        } catch (Exception e) {
            // Loga o erro mas não trava a criação do agendamento caso o n8n esteja desligado
            System.err.println("Erro ao enviar notificação para o n8n: " + e.getMessage());
        }
    }
}