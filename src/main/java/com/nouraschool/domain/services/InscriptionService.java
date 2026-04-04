package com.nouraschool.domain.services;

import com.nouraschool.domain.dtos.inscription.InscriptionCreateDto;
import com.nouraschool.domain.dtos.inscription.InscriptionDto;

import java.util.List;
import java.util.UUID;

public interface InscriptionService {

    List<InscriptionDto> findAll();

    InscriptionDto findById(UUID id);

    InscriptionDto create(InscriptionCreateDto dto, UUID creePar);

    InscriptionDto transferer(UUID id, UUID nouvelleClasseId);

    void delete(UUID id);
}
