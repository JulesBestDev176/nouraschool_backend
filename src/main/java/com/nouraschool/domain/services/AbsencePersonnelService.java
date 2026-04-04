package com.nouraschool.domain.services;

import com.nouraschool.domain.dtos.rh.AbsencePersonnelCreateDto;
import com.nouraschool.domain.dtos.rh.AbsencePersonnelDto;

import java.util.List;
import java.util.UUID;

public interface AbsencePersonnelService {

    List<AbsencePersonnelDto> findAll();

    AbsencePersonnelDto findById(UUID id);

    AbsencePersonnelDto create(AbsencePersonnelCreateDto dto);

    AbsencePersonnelDto valider(UUID id, UUID validatedBy);

    AbsencePersonnelDto refuser(UUID id, UUID validatedBy);
}
