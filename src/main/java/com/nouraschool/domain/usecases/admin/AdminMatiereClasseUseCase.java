package com.nouraschool.domain.usecases.admin;

import com.nouraschool.domain.dtos.MatiereClasseDto;

import java.util.List;
import java.util.UUID;

public interface AdminMatiereClasseUseCase {

    List<MatiereClasseDto> findAll();

    MatiereClasseDto findById(UUID id);

    MatiereClasseDto create(MatiereClasseDto dto);

    MatiereClasseDto update(UUID id, MatiereClasseDto dto);

    void delete(UUID id);
}
