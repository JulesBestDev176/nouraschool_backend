package com.nouraschool.domain.usecases.admin;

import com.nouraschool.domain.dtos.AbsenceEnseignantDto;

import java.util.List;
import java.util.UUID;

public interface AdminAbsenceEnseignantUseCase {

    List<AbsenceEnseignantDto> findAll();

    AbsenceEnseignantDto findById(UUID id);

    AbsenceEnseignantDto create(AbsenceEnseignantDto dto);

    AbsenceEnseignantDto update(UUID id, AbsenceEnseignantDto dto);

    void delete(UUID id);
}
