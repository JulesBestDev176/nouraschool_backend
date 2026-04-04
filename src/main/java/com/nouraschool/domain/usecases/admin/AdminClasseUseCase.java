package com.nouraschool.domain.usecases.admin;

import com.nouraschool.domain.dtos.ClasseDto;

import java.util.List;
import java.util.UUID;

public interface AdminClasseUseCase {

    List<ClasseDto> findAll();

    ClasseDto findById(UUID id);

    ClasseDto create(ClasseDto dto);

    ClasseDto update(UUID id, ClasseDto dto);

    void delete(UUID id);
}
