package com.fas.dto;

import java.time.LocalDate;

public class BodyProgressResponse {

    private LocalDate measurementDate;
    private Double weight;
    private Double bodyFat;
    private Double muscleMass;
    private Double smm;
    private Double waist;
    private Double hip;

    public BodyProgressResponse() {
    }

    public BodyProgressResponse(
            LocalDate measurementDate,
            Double weight,
            Double bodyFat,
            Double muscleMass,
            Double smm,
            Double waist,
            Double hip
    ) {
        this.measurementDate = measurementDate;
        this.weight = weight;
        this.bodyFat = bodyFat;
        this.muscleMass = muscleMass;
        this.smm = smm;
        this.waist = waist;
        this.hip = hip;
    }

    public LocalDate getMeasurementDate() {
        return measurementDate;
    }

    public void setMeasurementDate(LocalDate measurementDate) {
        this.measurementDate = measurementDate;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getBodyFat() {
        return bodyFat;
    }

    public void setBodyFat(Double bodyFat) {
        this.bodyFat = bodyFat;
    }

    public Double getMuscleMass() {
        return muscleMass;
    }

    public void setMuscleMass(Double muscleMass) {
        this.muscleMass = muscleMass;
    }

    public Double getSmm() {
        return smm;
    }

    public void setSmm(Double smm) {
        this.smm = smm;
    }

    public Double getWaist() {
        return waist;
    }

    public void setWaist(Double waist) {
        this.waist = waist;
    }

    public Double getHip() {
        return hip;
    }

    public void setHip(Double hip) {
        this.hip = hip;
    }
}