package com.nouraschool.domain.services;

import com.nouraschool.domain.dtos.etablissement.SalleDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SalleService {

    List<SalleDto> findAll(Optional<UUID> batimentId);

    SalleDto findById(UUID id);

    SalleDto create(SalleDto dto);

    SalleDto update(UUID id, SalleDto dto);

    void delete(UUID id);
}
