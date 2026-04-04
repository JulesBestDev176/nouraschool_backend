package com.nouraschool.domain.services;

/**
 * Service OTP (codes a 6 chiffres) stockes dans Redis avec TTL.
 */
public interface OtpService {

    /**
     * Genere un code OTP 6 chiffres, le stocke dans Redis avec TTL 300s.
     *
     * @param key cle Redis (ex: email ou telephone)
     * @return le code genere
     */
    String generate(String key);

    /**
     * Verifie le code OTP. Max 3 tentatives.
     *
     * @param key  cle Redis
     * @param code code saisi par l'utilisateur
     * @return true si valide, false sinon
     */
    boolean verify(String key, String code);

    /**
     * Invalide le code OTP (supprime de Redis).
     *
     * @param key cle Redis
     */
    void invalidate(String key);
}
