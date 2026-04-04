package com.nouraschool.domain.services.impl;

import com.nouraschool.domain.services.OtpService;
import com.nouraschool.domain.services.RedisService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.security.SecureRandom;

@ApplicationScoped
public class OtpServiceImpl implements OtpService {

    private static final int OTP_LENGTH = 6;
    private static final long OTP_TTL_SECONDS = 300;
    private static final int MAX_ATTEMPTS = 3;
    private static final String REDIS_KEY_OTP = "otp:";
    private static final String REDIS_KEY_ATTEMPTS = "otp:attempts:";

    private final RedisService redisService;
    private final SecureRandom secureRandom = new SecureRandom();

    @Inject
    public OtpServiceImpl(RedisService redisService) {
        this.redisService = redisService;
    }

    @Override
    public String generate(String key) {
        String code = generateSixDigitCode();
        redisService.set(REDIS_KEY_OTP + key, code, OTP_TTL_SECONDS);
        redisService.delete(REDIS_KEY_ATTEMPTS + key);
        return code;
    }

    @Override
    public boolean verify(String key, String code) {
        String stored = redisService.get(REDIS_KEY_OTP + key);
        if (stored == null) {
            return false;
        }
        int attempts = getAttempts(key);
        if (attempts >= MAX_ATTEMPTS) {
            invalidate(key);
            return false;
        }
        if (!stored.equals(code)) {
            redisService.set(REDIS_KEY_ATTEMPTS + key, String.valueOf(attempts + 1), OTP_TTL_SECONDS);
            return false;
        }
        invalidate(key);
        return true;
    }

    @Override
    public void invalidate(String key) {
        redisService.delete(REDIS_KEY_OTP + key);
        redisService.delete(REDIS_KEY_ATTEMPTS + key);
    }

    private String generateSixDigitCode() {
        int code = secureRandom.nextInt(1_000_000);
        return String.format("%06d", code);
    }

    private int getAttempts(String key) {
        String v = redisService.get(REDIS_KEY_ATTEMPTS + key);
        if (v == null || v.isBlank()) {
            return 0;
        }
        try {
            return Integer.parseInt(v.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
