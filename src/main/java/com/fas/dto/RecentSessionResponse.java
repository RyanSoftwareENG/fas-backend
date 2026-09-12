package com.fas.dto;

import java.time.LocalDateTime;

public class RecentSessionResponse {

    private Long sessionId;
    private Long clientId;
    private String clientName;
    private LocalDateTime sessionDate;
    private String duration;
    private Double price;
    private String notes;

    public RecentSessionResponse() {
    }

    public RecentSessionResponse(
            Long sessionId,
            Long clientId,
            String clientName,
            LocalDateTime sessionDate,
            String duration,
            Double price,
            String notes
    ) {
        this.sessionId = sessionId;
        this.clientId = clientId;
        this.clientName = clientName;
        this.sessionDate = sessionDate;
        this.duration = duration;
        this.price = price;
        this.notes = notes;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public Long getClientId() {
        return clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public LocalDateTime getSessionDate() {
        return sessionDate;
    }

    public String getDuration() {
        return duration;
    }

    public Double getPrice() {
        return price;
    }

    public String getNotes() {
        return notes;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public void setSessionDate(LocalDateTime sessionDate) {
        this.sessionDate = sessionDate;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}