package com.nouraschool.domain.services.impl;

import com.nouraschool.domain.services.WhatsAppService;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Implémentation stub — intégration WhatsApp Business API à brancher.
 * Log uniquement en attendant la configuration réelle.
 */
@ApplicationScoped
public class WhatsAppServiceImpl implements WhatsAppService {

    @Override
    public void sendAbsenceApprouvee(String telephone, String eleveNom, String date) {
        // TODO: intégrer WhatsApp Business API
    }

    @Override
    public void sendConvocation(String telephone, String eleveNom, String date, String heure, String motif) {
        // TODO: intégrer WhatsApp Business API
    }

    @Override
    public void sendBulletinDisponible(String telephone, String eleveNom, String trimestre, String lien) {
        // TODO: intégrer WhatsApp Business API
    }

    @Override
    public void sendRappelPaiement(String telephone, String montantDu, String echeance) {
        // TODO: intégrer WhatsApp Business API
    }
}
