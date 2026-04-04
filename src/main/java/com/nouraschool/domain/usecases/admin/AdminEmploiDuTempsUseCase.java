package com.nouraschool.domain.usecases.admin;

import com.nouraschool.domain.dtos.EmploiDuTempsDto;

import java.util.List;
import java.util.UUID;

public interface AdminEmploiDuTempsUseCase {

    List<EmploiDuTempsDto> findAll();

    List<EmploiDuTempsDto> findByClasseId(UUID classeId);

    EmploiDuTempsDto findById(UUID id);

    EmploiDuTempsDto create(EmploiDuTempsDto dto);

    EmploiDuTempsDto update(UUID id, EmploiDuTempsDto dto);

    void delete(UUID id);
}
