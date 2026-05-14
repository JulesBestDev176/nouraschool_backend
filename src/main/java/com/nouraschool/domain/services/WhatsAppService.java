package com.nouraschool.domain.services;

public interface WhatsAppService {

    void sendOtp(String telephone, String code);

    void sendAbsenceApprouvee(String telephone, String eleveNom, String date);

    void sendConvocation(String telephone, String eleveNom, String date, String heure, String motif);

    void sendBulletinDisponible(String telephone, String eleveNom, String trimestre, String lien);

    void sendRappelPaiement(String telephone, String montantDu, String echeance);
}
