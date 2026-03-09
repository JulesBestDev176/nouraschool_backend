package com.nouraschool.domain.services;

/**
 * Service OTP (codes Ã  6 chiffres) stockÃ©s dans Redis avec TTL.
 */
public interface OtpService {

    /**
     * GÃ©nÃ¨re un code OTP 6 chiffres, le stocke dans Redis avec TTL 300s.
     *
     * @param key clÃ© Redis (ex: email ou tÃ©lÃ©phone)
     * @return le code gÃ©nÃ©rÃ©
     */
    String generate(String key);

    /**
     * VÃ©rifie le code OTP. Max 3 tentatives.
     *
     * @param key  clÃ© Redis
     * @param code code saisi par l'utilisateur
     * @return true si valide, false sinon
     */
    boolean verify(String key, String code);

    /**
     * Invalide le code OTP (supprime de Redis).
     *
     * @param key clÃ© Redis
     */
    void invalidate(String key);
}
