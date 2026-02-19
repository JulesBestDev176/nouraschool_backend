package com.nouraschool.domain.usecases.admin;

import com.nouraschool.domain.dtos.MatiereDto;

import java.util.List;
import java.util.UUID;

public interface AdminMatiereUseCase {

    List<MatiereDto> findAll();

    MatiereDto findById(UUID id);

    MatiereDto create(MatiereDto dto);

    MatiereDto update(UUID id, MatiereDto dto);

    void delete(UUID id);
}
