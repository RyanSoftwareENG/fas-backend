package com.fas.config;

public final class ClinicContext {

    private static final ThreadLocal<Long> CURRENT_CLINIC_ID =
            new ThreadLocal<>();

    private ClinicContext() {
    }

    public static void setClinicId(Long clinicId) {

        if (clinicId == null) {
            throw new IllegalArgumentException(
                    "Clinic ID cannot be null"
            );
        }

        CURRENT_CLINIC_ID.set(clinicId);
    }

    public static Long getClinicId() {
        return CURRENT_CLINIC_ID.get();
    }

    public static void clear() {
        CURRENT_CLINIC_ID.remove();
    }
}