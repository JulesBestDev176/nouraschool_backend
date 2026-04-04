package com.nouraschool.domain.services.impl;

import com.nouraschool.domain.services.SmsService;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Implémentation stub — intégration Twilio à brancher.
 */
@ApplicationScoped
public class SmsServiceImpl implements SmsService {

    @Override
    public void sendSms(String telephone, String message) {
        // TODO: intégrer Twilio API
    }
}
