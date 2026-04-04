package com.nouraschool.domain.usecases.admin;

import com.nouraschool.domain.dtos.PaiementDto;

import java.util.List;
import java.util.UUID;

public interface AdminPaiementUseCase {

    List<PaiementDto> findAll();

    PaiementDto findById(UUID id);

    PaiementDto create(PaiementDto dto);

    PaiementDto update(UUID id, PaiementDto dto);

    void delete(UUID id);
}
