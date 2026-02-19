package com.nouraschool.domain.usecases.admin;

import com.nouraschool.domain.dtos.EmploiDuTempsDto;
import com.nouraschool.domain.entities.EmploiDuTempsEntity;
import com.nouraschool.domain.exception.errors.NotFoundException;
import com.nouraschool.domain.mappers.EmploiDuTempsMapper;
import com.nouraschool.domain.repositories.ClasseRepository;
import com.nouraschool.domain.repositories.EmploiDuTempsRepository;
import com.nouraschool.domain.repositories.EnseignantRepository;
import com.nouraschool.domain.repositories.MatiereRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class AdminEmploiDuTempsUseCaseImpl implements AdminEmploiDuTempsUseCase {

    @Inject
    EmploiDuTempsRepository emploiDuTempsRepository;
    @Inject
    ClasseRepository classeRepository;
    @Inject
    MatiereRepository matiereRepository;
    @Inject
    EnseignantRepository enseignantRepository;
    @Inject
    EmploiDuTempsMapper emploiDuTempsMapper;

    @Override
    public List<EmploiDuTempsDto> findAll() {
        return emploiDuTempsRepository.findAll().stream().map(emploiDuTempsMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public EmploiDuTempsDto findById(UUID id) {
        EmploiDuTempsEntity entity = emploiDuTempsRepository.findById(id);
        if (entity == null) throw new NotFoundException("EmploiDuTemps not found: " + id);
        return emploiDuTempsMapper.toDto(entity);
    }

    @Override
    @Transactional
    public EmploiDuTempsDto create(EmploiDuTempsDto dto) {
        EmploiDuTempsEntity entity = emploiDuTempsMapper.toEntity(dto);
        if (dto.getClasseId() != null) entity.classe = classeRepository.findById(dto.getClasseId());
        if (dto.getMatiereId() != null) entity.matiere = matiereRepository.findById(dto.getMatiereId());
        if (dto.getEnseignantId() != null) entity.enseignantEntity = enseignantRepository.findById(dto.getEnseignantId());
        return emploiDuTempsMapper.toDto(emploiDuTempsRepository.persist(entity));
    }

    @Override
    @Transactional
    public EmploiDuTempsDto update(UUID id, EmploiDuTempsDto dto) {
        EmploiDuTempsEntity entity = emploiDuTempsRepository.findById(id);
        if (entity == null) throw new NotFoundException("EmploiDuTemps not found: " + id);
        if (dto.getClasseId() != null) entity.classe = classeRepository.findById(dto.getClasseId());
        if (dto.getMatiereId() != null) entity.matiere = matiereRepository.findById(dto.getMatiereId());
        if (dto.getEnseignantId() != null) entity.enseignantEntity = enseignantRepository.findById(dto.getEnseignantId());
        if (dto.getJourSemaine() != null) entity.jourSemaine = dto.getJourSemaine();
        if (dto.getHeureDebut() != null) entity.heureDebut = dto.getHeureDebut();
        if (dto.getHeureFin() != null) entity.heureFin = dto.getHeureFin();
        if (dto.getSalle() != null) entity.salle = dto.getSalle();
        if (dto.getAnneeScolaire() != null) entity.anneeScolaire = dto.getAnneeScolaire();
        if (dto.getDateDebutValidite() != null) entity.dateDebutValidite = dto.getDateDebutValidite();
        if (dto.getDateFinValidite() != null) entity.dateFinValidite = dto.getDateFinValidite();
        return emploiDuTempsMapper.toDto(emploiDuTempsRepository.persist(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        EmploiDuTempsEntity entity = emploiDuTempsRepository.findById(id);
        if (entity == null) throw new NotFoundException("EmploiDuTemps not found: " + id);
        emploiDuTempsRepository.delete(entity);
    }
}
