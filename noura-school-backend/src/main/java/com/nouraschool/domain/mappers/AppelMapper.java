package com.nouraschool.domain.mappers;

import com.nouraschool.domain.dtos.AppelDto;
import com.nouraschool.domain.dtos.AppelLigneDto;
import com.nouraschool.domain.entities.AppelEntity;
import com.nouraschool.domain.entities.AppelLigneEntity;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Collections;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class AppelMapper {

    public AppelDto toDto(AppelEntity entity) {
        if (entity == null) return null;
        return AppelDto.builder()
                .id(entity.id)
                .coursId(entity.coursId)
                .dateCours(entity.dateCours)
                .heureDebut(entity.heureDebut)
                .statut(entity.statut)
                .soumisPar(entity.soumisPar)
                .createdAt(entity.createdAt)
                .lignes(entity.lignes != null ? entity.lignes.stream().map(this::toLigneDto).collect(Collectors.toList()) : Collections.emptyList())
                .build();
    }

    public AppelLigneDto toLigneDto(AppelLigneEntity entity) {
        if (entity == null) return null;
        return AppelLigneDto.builder()
                .id(entity.id)
                .eleveId(entity.eleveId)
                .statut(entity.statut)
                .build();
    }

    public AppelLigneEntity toLigneEntity(AppelLigneDto dto, UUID appelId) {
        if (dto == null) return null;
        AppelLigneEntity e = new AppelLigneEntity();
        e.appelId = appelId;
        e.eleveId = dto.getEleveId();
        e.statut = dto.getStatut();
        return e;
    }
}
