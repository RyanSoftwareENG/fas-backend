package com.fas.dto;

public class PatientProgressSummary {

    private Double firstWeight;
    private Double currentWeight;
    private Double weightChange;

    private Double firstBodyFat;
    private Double currentBodyFat;
    private Double bodyFatChange;

    private Double firstMuscleMass;
    private Double currentMuscleMass;
    private Double muscleMassChange;

    public PatientProgressSummary() {
    }

    public Double getFirstWeight() {
        return firstWeight;
    }

    public void setFirstWeight(Double firstWeight) {
        this.firstWeight = firstWeight;
    }

    public Double getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(Double currentWeight) {
        this.currentWeight = currentWeight;
    }

    public Double getWeightChange() {
        return weightChange;
    }

    public void setWeightChange(Double weightChange) {
        this.weightChange = weightChange;
    }

    public Double getFirstBodyFat() {
        return firstBodyFat;
    }

    public void setFirstBodyFat(Double firstBodyFat) {
        this.firstBodyFat = firstBodyFat;
    }

    public Double getCurrentBodyFat() {
        return currentBodyFat;
    }

    public void setCurrentBodyFat(Double currentBodyFat) {
        this.currentBodyFat = currentBodyFat;
    }

    public Double getBodyFatChange() {
        return bodyFatChange;
    }

    public void setBodyFatChange(Double bodyFatChange) {
        this.bodyFatChange = bodyFatChange;
    }

    public Double getFirstMuscleMass() {
        return firstMuscleMass;
    }

    public void setFirstMuscleMass(Double firstMuscleMass) {
        this.firstMuscleMass = firstMuscleMass;
    }

    public Double getCurrentMuscleMass() {
        return currentMuscleMass;
    }

    public void setCurrentMuscleMass(Double currentMuscleMass) {
        this.currentMuscleMass = currentMuscleMass;
    }

    public Double getMuscleMassChange() {
        return muscleMassChange;
    }

    public void setMuscleMassChange(Double muscleMassChange) {
        this.muscleMassChange = muscleMassChange;
    }
}