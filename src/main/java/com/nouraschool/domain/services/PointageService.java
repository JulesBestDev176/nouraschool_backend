package com.nouraschool.domain.services;

import com.nouraschool.domain.dtos.rh.PointageCreateDto;
import com.nouraschool.domain.dtos.rh.PointageDto;
import com.nouraschool.domain.dtos.rh.PointageRapportDto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface PointageService {

    List<PointageDto> findAll();

    PointageDto findById(UUID id);

    PointageDto create(PointageCreateDto dto);

    PointageRapportDto getRapport(Instant debut, Instant fin);
}
