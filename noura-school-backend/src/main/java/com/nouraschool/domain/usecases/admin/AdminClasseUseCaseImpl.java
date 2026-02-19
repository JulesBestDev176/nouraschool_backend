package com.nouraschool.domain.usecases.admin;

import com.nouraschool.domain.dtos.ClasseDto;
import com.nouraschool.domain.entities.ClasseEntity;
import com.nouraschool.domain.exception.errors.NotFoundException;
import com.nouraschool.domain.mappers.ClasseMapper;
import com.nouraschool.domain.repositories.ClasseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class AdminClasseUseCaseImpl implements AdminClasseUseCase {

    @Inject
    ClasseRepository classeRepository;
    @Inject
    ClasseMapper classeMapper;

    @Override
    public List<ClasseDto> findAll() {
        return classeRepository.findAll().stream().map(classeMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public ClasseDto findById(UUID id) {
        ClasseEntity entity = classeRepository.findById(id);
        if (entity == null) throw new NotFoundException("Classe not found: " + id);
        return classeMapper.toDto(entity);
    }

    @Override
    @Transactional
    public ClasseDto create(ClasseDto dto) {
        ClasseEntity entity = classeMapper.toEntity(dto);
        return classeMapper.toDto(classeRepository.persist(entity));
    }

    @Override
    @Transactional
    public ClasseDto update(UUID id, ClasseDto dto) {
        ClasseEntity entity = classeRepository.findById(id);
        if (entity == null) throw new NotFoundException("Classe not found: " + id);
        entity.nom = dto.getNom();
        entity.niveau = dto.getNiveau();
        entity.anneeScolaire = dto.getAnneeScolaire();
        entity.effectifMax = dto.getEffectifMax();
        entity.salleClasse = dto.getSalleClasse();
        return classeMapper.toDto(classeRepository.persist(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        ClasseEntity entity = classeRepository.findById(id);
        if (entity == null) throw new NotFoundException("Classe not found: " + id);
        classeRepository.delete(entity);
    }
}
