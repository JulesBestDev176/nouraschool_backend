package com.nouraschool.domain.services;

import com.nouraschool.domain.dtos.etablissement.AnneeAcademiqueDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AnneeAcademiqueService {

    List<AnneeAcademiqueDto> findAll();

    Optional<AnneeAcademiqueDto> findCourante();

    AnneeAcademiqueDto findById(UUID id);

    AnneeAcademiqueDto create(AnneeAcademiqueDto dto);

    AnneeAcademiqueDto update(UUID id, AnneeAcademiqueDto dto);

    void delete(UUID id);

    AnneeAcademiqueDto activer(UUID id);
}
