package com.nouraschool.domain.usecases.admin;

import com.nouraschool.domain.dtos.ReclamationDto;

import java.util.List;
import java.util.UUID;

public interface AdminReclamationUseCase {

    List<ReclamationDto> findAll();

    ReclamationDto findById(UUID id);

    ReclamationDto create(ReclamationDto dto);

    ReclamationDto update(UUID id, ReclamationDto dto);

    void delete(UUID id);
}
