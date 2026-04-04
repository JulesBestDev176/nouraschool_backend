package com.nouraschool.domain.services;

import com.nouraschool.domain.dtos.etablissement.NiveauDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NiveauService {

    List<NiveauDto> findAll(Optional<UUID> cycleId);

    NiveauDto findById(UUID id);

    NiveauDto create(NiveauDto dto);

    NiveauDto update(UUID id, NiveauDto dto);

    void delete(UUID id);
}
