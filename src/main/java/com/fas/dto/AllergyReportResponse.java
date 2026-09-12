package com.fas.dto;

public class AllergyReportResponse {

    private String allergyName;
    private long patientCount;

    public AllergyReportResponse() {
    }

    public AllergyReportResponse(
            String allergyName,
            long patientCount
    ) {
        this.allergyName = allergyName;
        this.patientCount = patientCount;
    }

    public String getAllergyName() {
        return allergyName;
    }

    public void setAllergyName(String allergyName) {
        this.allergyName = allergyName;
    }

    public long getPatientCount() {
        return patientCount;
    }

    public void setPatientCount(long patientCount) {
        this.patientCount = patientCount;
    }
}