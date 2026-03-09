package com.nouraschool.domain.services;

import com.nouraschool.domain.dtos.etablissement.CoursCreateDto;
import com.nouraschool.domain.dtos.etablissement.CoursDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CoursService {

    List<CoursDto> findAll(Optional<UUID> classeId);

    CoursDto findById(UUID id);

    CoursDto create(CoursCreateDto dto);

    CoursDto update(UUID id, CoursDto dto);

    void delete(UUID id);
}
