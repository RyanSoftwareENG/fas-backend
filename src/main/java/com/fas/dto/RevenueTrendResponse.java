package com.fas.dto;

import java.time.LocalDate;

public class RevenueTrendResponse {

    private LocalDate date;
    private double revenue;

    public RevenueTrendResponse() {
    }

    public RevenueTrendResponse(
            LocalDate date,
            double revenue
    ) {
        this.date = date;
        this.revenue = revenue;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }
}