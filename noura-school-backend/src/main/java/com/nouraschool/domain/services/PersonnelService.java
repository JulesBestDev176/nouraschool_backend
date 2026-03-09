package com.nouraschool.domain.services;

import com.nouraschool.domain.dtos.rh.PersonnelCreateDto;
import com.nouraschool.domain.dtos.rh.PersonnelDto;

import java.util.List;
import java.util.UUID;

public interface PersonnelService {

    List<PersonnelDto> findAll();

    PersonnelDto findById(UUID id);

    PersonnelDto create(PersonnelCreateDto dto);

    PersonnelDto update(UUID id, PersonnelCreateDto dto);

    void delete(UUID id);
}
