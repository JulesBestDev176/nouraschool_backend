package com.nouraschool.domain.services;

import com.nouraschool.domain.dtos.etablissement.AnnonceCreateDto;
import com.nouraschool.domain.dtos.etablissement.AnnonceDto;

import java.util.List;
import java.util.UUID;

public interface AnnonceService {

    List<AnnonceDto> findAll();

    AnnonceDto findById(UUID id);

    AnnonceDto create(AnnonceCreateDto dto);

    AnnonceDto update(UUID id, AnnonceCreateDto dto);

    void delete(UUID id);
}
