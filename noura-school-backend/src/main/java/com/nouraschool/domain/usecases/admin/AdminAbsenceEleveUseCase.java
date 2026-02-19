package com.nouraschool.domain.usecases.admin;

import com.nouraschool.domain.dtos.AbsenceEleveDto;

import java.util.List;
import java.util.UUID;

public interface AdminAbsenceEleveUseCase {

    List<AbsenceEleveDto> findAll();

    AbsenceEleveDto findById(UUID id);

    AbsenceEleveDto create(AbsenceEleveDto dto);

    AbsenceEleveDto update(UUID id, AbsenceEleveDto dto);

    void delete(UUID id);
}
