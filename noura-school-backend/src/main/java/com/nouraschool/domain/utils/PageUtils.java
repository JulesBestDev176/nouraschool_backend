package com.nouraschool.domain.utils;

import com.nouraschool.domain.dtos.PageRequest;

import java.util.Set;

/**
 * Helper pour la pagination standard (PageRequest / PageDto).
 * Constantes et factory avec valeurs par défaut et bornes.
 */
public final class PageUtils {

    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;

    private PageUtils() {
    }

    /**
     * Construit un {@link PageRequest} à partir des paramètres de requête, avec bornes.
     *
     * @param page      numéro de page (0-based) ; &lt; 0 est ramené à 0
     * @param size      taille de page ; borné entre 1 et {@link #MAX_SIZE}
     * @param sortBy    champ de tri (nullable)
     * @param ascending ordre ascendant
     * @return PageRequest avec valeurs valides
     */
    public static PageRequest of(int page, int size, String sortBy, boolean ascending) {
        int p = Math.max(0, page);
        int s = size <= 0 ? DEFAULT_SIZE : Math.min(Math.max(1, size), MAX_SIZE);
        return PageRequest.builder()
                .page(p)
                .size(s)
                .sortBy(sortBy != null && !sortBy.isBlank() ? sortBy.trim() : null)
                .ascending(ascending)
                .build();
    }

    /**
     * Construit un PageRequest avec taille par défaut.
     */
    public static PageRequest of(int page, int size) {
        return of(page, size, null, true);
    }

    /**
     * Retourne un champ de tri sûr pour ORDER BY : doit appartenir à l'ensemble autorisé.
     *
     * @param sortBy        valeur demandée (nullable)
     * @param allowedFields champs autorisés (ex. "id", "nom", "createdAt")
     * @param defaultSort   valeur par défaut si sortBy null ou non autorisé
     * @return champ à utiliser dans la requête
     */
    public static String sanitizeSort(String sortBy, Set<String> allowedFields, String defaultSort) {
        if (sortBy == null || sortBy.isBlank()) {
            return defaultSort;
        }
        String candidate = sortBy.trim();
        return allowedFields.contains(candidate) ? candidate : defaultSort;
    }
}
