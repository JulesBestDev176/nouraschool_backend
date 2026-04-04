package com.nouraschool.domain.services;

import com.nouraschool.domain.dtos.viescolaire.ConvocationCreateDto;
import com.nouraschool.domain.dtos.viescolaire.ConvocationDto;

import java.util.List;
import java.util.UUID;

public interface ConvocationService {

    List<ConvocationDto> findAll();

    ConvocationDto findById(UUID id);

    ConvocationDto create(ConvocationCreateDto dto, UUID creePar);

    ConvocationDto updateCompteRendu(UUID id, String compteRendu);

    void delete(UUID id);
}
