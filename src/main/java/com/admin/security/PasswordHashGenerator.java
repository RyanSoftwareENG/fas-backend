package com.admin.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {

    public static void main(String[] args) {

        // كلمة المرور التي تريد تخزين Hash الخاص بها
        String password = "RyanAdmin1237890";

        // BCrypt مع strength = 12
        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder(12);

        // توليد Hash مرة واحدة
        String hash = encoder.encode(password);

        System.out.println("==========================================");
        System.out.println("Password Hash:");
        System.out.println(hash);
        System.out.println("==========================================");
    }
}