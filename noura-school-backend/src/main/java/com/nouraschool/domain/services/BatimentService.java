package com.nouraschool.domain.services;

import com.nouraschool.domain.dtos.etablissement.BatimentDto;

import java.util.List;
import java.util.UUID;

public interface BatimentService {

    List<BatimentDto> findAll();

    BatimentDto findById(UUID id);

    BatimentDto create(BatimentDto dto);

    BatimentDto update(UUID id, BatimentDto dto);

    void delete(UUID id);
}
