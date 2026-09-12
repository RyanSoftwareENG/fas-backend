package com.fas.dto;

public class ReportAlertResponse {

    private String type;
    private String severity;
    private String title;
    private String message;
    private long count;

    public ReportAlertResponse() {
    }

    public ReportAlertResponse(
            String type,
            String severity,
            String title,
            String message,
            long count
    ) {
        this.type = type;
        this.severity = severity;
        this.title = title;
        this.message = message;
        this.count = count;
    }

    public String getType() {
        return type;
    }

    public String getSeverity() {
        return severity;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public long getCount() {
        return count;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCount(long count) {
        this.count = count;
    }
}