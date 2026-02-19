package com.nouraschool.domain.usecases.admin;

import com.nouraschool.domain.dtos.ParentCreateDto;
import com.nouraschool.domain.dtos.ParentDto;

import java.util.List;
import java.util.UUID;

public interface AdminParentUseCase {

    List<ParentDto> findAll();

    ParentDto findById(UUID id);

    ParentDto create(ParentCreateDto dto);

    ParentDto update(UUID id, ParentDto dto);

    void delete(UUID id);
}
