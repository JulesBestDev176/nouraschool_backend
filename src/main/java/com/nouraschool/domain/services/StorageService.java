package com.nouraschool.domain.services;

import java.io.InputStream;
import java.util.Optional;

/**
 * Stockage de fichiers sur S3 (documents, bulletins). Les fichiers ne sont jamais servis directement
 * par l'API — accès via URLs pré-signées temporaires (task.md).
 */
public interface StorageService {

    /**
     * Envoie un objet dans le bucket S3.
     *
     * @param key         clé de l'objet (chemin logique)
     * @param input       contenu du fichier
     * @param contentType type MIME (ex. application/pdf)
     * @param contentLength taille en octets (optionnel, -1 si inconnu)
     * @return la clé de l'objet stocké
     */
    String upload(String key, InputStream input, String contentType, long contentLength);

    /**
     * Génère une URL pré-signée pour télécharger l'objet (GET). Expiration configurée (ex. 15 min).
     *
     * @param key clé de l'objet
     * @return URL pré-signée ou empty si l'objet n'existe pas / erreur
     */
    Optional<String> getPresignedUrl(String key);

    /**
     * Supprime un objet du bucket.
     *
     * @param key clé de l'objet
     */
    void delete(String key);
}
