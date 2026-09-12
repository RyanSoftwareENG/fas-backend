package com.fas.dto;

public class ChronicDiseaseReportResponse {

    private String diseaseName;
    private long patientCount;

    public ChronicDiseaseReportResponse() {
    }

    public ChronicDiseaseReportResponse(
            String diseaseName,
            long patientCount
    ) {
        this.diseaseName = diseaseName;
        this.patientCount = patientCount;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    public long getPatientCount() {
        return patientCount;
    }

    public void setPatientCount(long patientCount) {
        this.patientCount = patientCount;
    }
}