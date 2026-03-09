package com.nouraschool.domain.services;

/**
 * Service SMS (Twilio, OTP fallback) — task.md section 15.
 */
public interface SmsService {

    void sendSms(String telephone, String message);
}
