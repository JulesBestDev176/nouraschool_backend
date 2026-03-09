package com.nouraschool.domain.services;

import com.nouraschool.domain.dtos.etablissement.CycleDto;

import java.util.List;
import java.util.UUID;

public interface CycleService {

    List<CycleDto> findAll();

    CycleDto findById(UUID id);

    CycleDto create(CycleDto dto);

    CycleDto update(UUID id, CycleDto dto);

    void delete(UUID id);
}
