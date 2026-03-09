package com.nouraschool.domain.services;

import com.nouraschool.domain.dtos.platform.PlateformeUtilisateurCreateDto;
import com.nouraschool.domain.dtos.platform.PlateformeUtilisateurDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlateformeUtilisateurService {

    List<PlateformeUtilisateurDto> findAll();

    Optional<PlateformeUtilisateurDto> findById(UUID id);

    PlateformeUtilisateurDto create(PlateformeUtilisateurCreateDto dto);

    void delete(UUID id);

    void setActif(UUID id, boolean actif);
}
