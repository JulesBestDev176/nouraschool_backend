package com.nouraschool.domain.usecases.admin;

import com.nouraschool.domain.dtos.CalendrierScolaireDto;

import java.util.List;
import java.util.UUID;

public interface AdminCalendrierScolaireUseCase {

    List<CalendrierScolaireDto> findAll();

    CalendrierScolaireDto findById(UUID id);

    CalendrierScolaireDto create(CalendrierScolaireDto dto);

    CalendrierScolaireDto update(UUID id, CalendrierScolaireDto dto);

    void delete(UUID id);
}
