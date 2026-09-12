package com.fas.security;

public class UserSessionContext {

    private static final ThreadLocal<Long> USER_ID =
            new ThreadLocal<>();

    private static final ThreadLocal<Long> CLINIC_ID =
            new ThreadLocal<>();

    public static void set(
            Long userId,
            Long clinicId
    ) {
        USER_ID.set(userId);
        CLINIC_ID.set(clinicId);
    }

    public static Long getUserId() {
        return USER_ID.get();
    }

    public static Long getClinicId() {
        return CLINIC_ID.get();
    }

    public static void clear() {
        USER_ID.remove();
        CLINIC_ID.remove();
    }
}