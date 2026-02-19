package com.nouraschool.domain.usecases.admin;

import com.nouraschool.domain.dtos.EleveCreateDto;
import com.nouraschool.domain.dtos.EleveDto;
import com.nouraschool.domain.dtos.EleveUpdateDto;
import com.nouraschool.domain.dtos.PageDto;
import com.nouraschool.domain.dtos.PageRequest;
import com.nouraschool.domain.entities.EleveEntity;
import com.nouraschool.domain.enums.UserRole;
import com.nouraschool.domain.exception.errors.InvalidRequestException;
import com.nouraschool.domain.exception.errors.NotFoundException;
import com.nouraschool.domain.exception.errors.ServiceException;
import com.nouraschool.domain.mappers.EleveMapper;
import com.nouraschool.domain.repositories.ClasseRepository;
import com.nouraschool.domain.repositories.EleveRepository;
import com.nouraschool.domain.repositories.ParentRepository;
import com.nouraschool.domain.repositories.UserRepository;
import com.nouraschool.domain.services.PasswordEncoder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class AdminEleveUseCaseImpl implements AdminEleveUseCase {

    @Inject
    EleveRepository eleveRepository;
    @Inject
    ClasseRepository classeRepository;
    @Inject
    ParentRepository parentRepository;
    @Inject
    UserRepository userRepository;
    @Inject
    EleveMapper eleveMapper;
    @Inject
    PasswordEncoder passwordEncoder;

    @Override
    public List<EleveDto> findAll() {
        return eleveRepository.findAll().stream()
                .map(eleveMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PageDto<EleveDto> findAll(PageRequest pageRequest) {
        try {
            var entities = eleveRepository.findAll(pageRequest);
            var total = eleveRepository.count();
            var content = entities.stream().map(eleveMapper::toDto).collect(Collectors.toList());
            return PageDto.of(content, pageRequest.getPage(), pageRequest.getSize(), total);
        } catch (Exception e) {
            throw new ServiceException("Erreur pagination élèves: " + e.getMessage(), 500);
        }
    }

    @Override
    public EleveDto findById(UUID id) {
        var entity = eleveRepository.findById(id);
        if (entity == null) throw new NotFoundException("Eleve not found: " + id);
        return eleveMapper.toDto(entity);
    }

    @Override
    @Transactional
    public EleveDto create(EleveCreateDto dto) {
        try {
            if (userRepository.findByUsername(dto.getUsername()) != null) throw new InvalidRequestException("USERNAME_EXISTS");
            if (userRepository.findByUsernameOrEmail(dto.getEmail()) != null) throw new InvalidRequestException("EMAIL_EXISTS");
            var entity = new EleveEntity();
            mapCreateToEntity(dto, entity);
            entity.role = UserRole.ELEVE;
            entity.password = passwordEncoder.encode(dto.getPassword());
            return eleveMapper.toDto(eleveRepository.persist(entity));
        } catch (InvalidRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Erreur création élève: " + e.getMessage(), 500);
        }
    }

    @Override
    @Transactional
    public EleveDto update(UUID id, EleveUpdateDto dto) {
        try {
            var entity = eleveRepository.findById(id);
            if (entity == null) throw new NotFoundException("Eleve not found: " + id);
            mapUpdateToEntity(dto, entity);
            return eleveMapper.toDto(eleveRepository.persist(entity));
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Erreur mise à jour élève: " + e.getMessage(), 500);
        }
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        try {
            var entity = eleveRepository.findById(id);
            if (entity == null) throw new NotFoundException("Eleve not found: " + id);
            eleveRepository.delete(entity);
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Erreur suppression élève: " + e.getMessage(), 500);
        }
    }

    private void mapCreateToEntity(EleveCreateDto dto, EleveEntity entity) {
        entity.username = dto.getUsername();
        entity.email = dto.getEmail();
        entity.firstName = dto.getFirstName();
        entity.lastName = dto.getLastName();
        entity.telephone = dto.getTelephone();
        entity.adresse = dto.getAdresse();
        entity.matricule = dto.getMatricule();
        entity.dateNaissance = dto.getDateNaissance();
        entity.lieuNaissance = dto.getLieuNaissance();
        entity.genre = dto.getGenre();
        entity.numeroUrgence = dto.getNumeroUrgence();
        entity.dateInscription = dto.getDateInscription();
        entity.photoUrl = dto.getPhotoUrl();
        if (dto.getClasseId() != null) {
            entity.classe = classeRepository.findById(dto.getClasseId());
        }
        if (dto.getParentIds() != null && !dto.getParentIds().isEmpty()) {
            entity.parents = dto.getParentIds().stream()
                    .map(parentRepository::findById)
                    .filter(p -> p != null)
                    .collect(Collectors.toList());
        } else {
            entity.parents = new ArrayList<>();
        }
    }

    private void mapUpdateToEntity(EleveUpdateDto dto, EleveEntity entity) {
        if (dto.getUsername() != null) entity.username = dto.getUsername();
        if (dto.getEmail() != null) entity.email = dto.getEmail();
        if (dto.getFirstName() != null) entity.firstName = dto.getFirstName();
        if (dto.getLastName() != null) entity.lastName = dto.getLastName();
        if (dto.getTelephone() != null) entity.telephone = dto.getTelephone();
        if (dto.getAdresse() != null) entity.adresse = dto.getAdresse();
        if (dto.getActive() != null) entity.active = dto.getActive();
        if (dto.getMatricule() != null) entity.matricule = dto.getMatricule();
        if (dto.getDateNaissance() != null) entity.dateNaissance = dto.getDateNaissance();
        if (dto.getLieuNaissance() != null) entity.lieuNaissance = dto.getLieuNaissance();
        if (dto.getGenre() != null) entity.genre = dto.getGenre();
        if (dto.getNumeroUrgence() != null) entity.numeroUrgence = dto.getNumeroUrgence();
        if (dto.getDateInscription() != null) entity.dateInscription = dto.getDateInscription();
        if (dto.getPhotoUrl() != null) entity.photoUrl = dto.getPhotoUrl();
        if (dto.getClasseId() != null) {
            entity.classe = classeRepository.findById(dto.getClasseId());
        }
        if (dto.getParentIds() != null) {
            entity.parents = dto.getParentIds().isEmpty() ? new ArrayList<>() :
                    dto.getParentIds().stream()
                            .map(parentRepository::findById)
                            .filter(p -> p != null)
                            .collect(Collectors.toList());
        }
    }
}
