package com.nouraschool.domain.services;

import com.nouraschool.domain.dtos.platform.TenantCreateDto;
import com.nouraschool.domain.dtos.platform.TenantDto;
import com.nouraschool.domain.dtos.platform.TenantUpdateDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TenantService {

    List<TenantDto> findAll();

    Optional<TenantDto> findById(UUID id);

    TenantDto create(TenantCreateDto dto);

    TenantDto update(UUID id, TenantUpdateDto dto);

    void suspend(UUID id);

    void reactivate(UUID id);

    void delete(UUID id);

    /** Crée un tenant si le slug n'existe pas (slug dérivé du nom si absent). */
    TenantDto autoRegister(String nom, String slug);

    /**
     * Uploade le logo d'un tenant vers MinIO.
     * Chemin dans le bucket : {@code tenants/{slug}/logos/logo.{extension}}
     *
     * @return URL publique permanente du logo
     */
    String uploadLogo(java.util.UUID id, java.io.InputStream input, String contentType, long contentLength, String extension);
}
