package com.nouraschool.domain.usecases.admin;

import com.nouraschool.domain.dtos.EnseignantCreateDto;
import com.nouraschool.domain.dtos.EnseignantDto;

import java.util.List;
import java.util.UUID;

public interface AdminEnseignantUseCase {

    List<EnseignantDto> findAll();

    EnseignantDto findById(UUID id);

    EnseignantDto create(EnseignantCreateDto dto);

    EnseignantDto update(UUID id, EnseignantDto dto);

    void delete(UUID id);
}
