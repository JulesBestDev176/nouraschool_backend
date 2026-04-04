package com.nouraschool.domain.services;

/**
 * Service utilitaire Redis (get/set/delete/expire).
 * Utilisé pour rate limiting (tentatives login), OTP, tokens reset — task.md.
 */
public interface RedisService {

    /**
     * Récupère la valeur associée à la clé.
     *
     * @param key clé Redis
     * @return la valeur ou null si absente
     */
    String get(String key);

    /**
     * Enregistre une valeur pour la clé.
     *
     * @param key   clé Redis
     * @param value valeur (string)
     */
    void set(String key, String value);

    /**
     * Enregistre une valeur avec TTL en secondes.
     *
     * @param key     clé Redis
     * @param value   valeur
     * @param seconds durée de vie en secondes
     */
    void set(String key, String value, long seconds);

    /**
     * Supprime la clé.
     *
     * @param key clé Redis
     */
    void delete(String key);

    /**
     * Définit un TTL sur une clé existante.
     *
     * @param key     clé Redis
     * @param seconds durée en secondes
     */
    void expire(String key, long seconds);
}
