package com.vetcare_back.util;

import java.security.SecureRandom;

public class OtpUtil {

    private static final SecureRandom random = new SecureRandom();

    /**
     * Genera un OTP de 6 dígitos
     */
    public static String generateOtp() {
        int otp = random.nextInt(900000) + 100000; // Entre 100000 y 999999
        return String.valueOf(otp);
    }
}