package com.nouraschool.domain.usecases.admin;

import com.nouraschool.domain.dtos.EleveCreateDto;
import com.nouraschool.domain.dtos.EleveDto;
import com.nouraschool.domain.dtos.EleveUpdateDto;
import com.nouraschool.domain.dtos.PageDto;
import com.nouraschool.domain.dtos.PageRequest;

import java.util.List;
import java.util.UUID;

public interface AdminEleveUseCase {

    List<EleveDto> findAll();

    PageDto<EleveDto> findAll(PageRequest pageRequest);

    EleveDto findById(UUID id);

    EleveDto create(EleveCreateDto dto);

    EleveDto update(UUID id, EleveUpdateDto dto);

    void delete(UUID id);
}
