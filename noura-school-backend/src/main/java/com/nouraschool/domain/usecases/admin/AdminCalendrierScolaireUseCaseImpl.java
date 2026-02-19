package com.nouraschool.domain.usecases.admin;

import com.nouraschool.domain.dtos.CalendrierScolaireDto;
import com.nouraschool.domain.entities.CalendrierScolaireEntity;
import com.nouraschool.domain.exception.errors.NotFoundException;
import com.nouraschool.domain.mappers.CalendrierScolaireMapper;
import com.nouraschool.domain.repositories.CalendrierScolaireRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class AdminCalendrierScolaireUseCaseImpl implements AdminCalendrierScolaireUseCase {

    @Inject
    CalendrierScolaireRepository calendrierScolaireRepository;
    @Inject
    CalendrierScolaireMapper calendrierScolaireMapper;

    @Override
    public List<CalendrierScolaireDto> findAll() {
        return calendrierScolaireRepository.findAll().stream().map(calendrierScolaireMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public CalendrierScolaireDto findById(UUID id) {
        CalendrierScolaireEntity entity = calendrierScolaireRepository.findById(id);
        if (entity == null) throw new NotFoundException("CalendrierScolaire not found: " + id);
        return calendrierScolaireMapper.toDto(entity);
    }

    @Override
    @Transactional
    public CalendrierScolaireDto create(CalendrierScolaireDto dto) {
        CalendrierScolaireEntity entity = calendrierScolaireMapper.toEntity(dto);
        return calendrierScolaireMapper.toDto(calendrierScolaireRepository.persist(entity));
    }

    @Override
    @Transactional
    public CalendrierScolaireDto update(UUID id, CalendrierScolaireDto dto) {
        CalendrierScolaireEntity entity = calendrierScolaireRepository.findById(id);
        if (entity == null) throw new NotFoundException("CalendrierScolaire not found: " + id);
        entity.anneeScolaire = dto.getAnneeScolaire();
        entity.titre = dto.getTitre();
        entity.description = dto.getDescription();
        entity.typeEvenement = dto.getTypeEvenement();
        entity.dateDebut = dto.getDateDebut();
        entity.dateFin = dto.getDateFin();
        entity.concerneClasses = dto.getConcerneClasses();
        entity.publier = dto.getPublier() != null ? dto.getPublier() : true;
        return calendrierScolaireMapper.toDto(calendrierScolaireRepository.persist(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        CalendrierScolaireEntity entity = calendrierScolaireRepository.findById(id);
        if (entity == null) throw new NotFoundException("CalendrierScolaire not found: " + id);
        calendrierScolaireRepository.delete(entity);
    }
}
