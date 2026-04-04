package com.nouraschool.domain.usecases.admin;

import com.nouraschool.domain.dtos.ParentCreateDto;
import com.nouraschool.domain.dtos.ParentDto;
import com.nouraschool.domain.entities.ParentEntity;
import com.nouraschool.domain.enums.UserRole;
import com.nouraschool.domain.exception.errors.InvalidRequestException;
import com.nouraschool.domain.exception.errors.NotFoundException;
import com.nouraschool.domain.mappers.ParentMapper;
import com.nouraschool.domain.repositories.ParentRepository;
import com.nouraschool.domain.repositories.TenantRepository;
import com.nouraschool.domain.repositories.UserRepository;
import com.nouraschool.domain.services.PasswordEncoder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class AdminParentUseCaseImpl implements AdminParentUseCase {

    @Inject
    ParentRepository parentRepository;
    @Inject
    UserRepository userRepository;
    @Inject
    TenantRepository tenantRepository;
    @Inject
    ParentMapper parentMapper;
    @Inject
    PasswordEncoder passwordEncoder;

    @Override
    public List<ParentDto> findAll() {
        return parentRepository.findAll().stream().map(parentMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public ParentDto findById(UUID id) {
        ParentEntity entity = parentRepository.findById(id);
        if (entity == null) throw new NotFoundException("Parent not found: " + id);
        return parentMapper.toDto(entity);
    }

    @Override
    @Transactional
    public ParentDto create(ParentCreateDto dto) {
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new InvalidRequestException("VALIDATION_ECHOUEE");
        }
        if (dto.getFirstName() == null || dto.getFirstName().isBlank()
                || dto.getLastName() == null || dto.getLastName().isBlank()) {
            throw new InvalidRequestException("VALIDATION_ECHOUEE");
        }
        String username = (dto.getUsername() != null && !dto.getUsername().isBlank())
                ? dto.getUsername()
                : dto.getEmail();
        String password = (dto.getPassword() != null && !dto.getPassword().isBlank())
                ? dto.getPassword()
                : "Parent123!";

        if (userRepository.findByUsername(username) != null) throw new InvalidRequestException("EMAIL_DEJA_UTILISE");
        if (userRepository.findByUsernameOrEmail(dto.getEmail()) != null) throw new InvalidRequestException("EMAIL_DEJA_UTILISE");
        ParentEntity entity = new ParentEntity();
        entity.tenant = tenantRepository.findDefault();
        entity.username = username;
        entity.email = dto.getEmail();
        entity.password = passwordEncoder.encode(password);
        entity.firstName = dto.getFirstName();
        entity.lastName = dto.getLastName();
        entity.telephone = dto.getTelephone();
        entity.adresse = dto.getAdresse();
        entity.role = UserRole.PARENT;
        entity.mustChangePassword = true;
        entity.profession = dto.getProfession();
        entity.lieuTravail = dto.getLieuTravail();
        entity.telephoneTravail = dto.getTelephoneTravail();
        entity.lienParente = dto.getLienParente();
        return parentMapper.toDto(parentRepository.persist(entity));
    }

    @Override
    @Transactional
    public ParentDto update(UUID id, ParentDto dto) {
        ParentEntity entity = parentRepository.findById(id);
        if (entity == null) throw new NotFoundException("Parent not found: " + id);
        if (dto.getUsername() != null) entity.username = dto.getUsername();
        if (dto.getEmail() != null) entity.email = dto.getEmail();
        if (dto.getFirstName() != null) entity.firstName = dto.getFirstName();
        if (dto.getLastName() != null) entity.lastName = dto.getLastName();
        if (dto.getTelephone() != null) entity.telephone = dto.getTelephone();
        if (dto.getAdresse() != null) entity.adresse = dto.getAdresse();
        if (dto.getActive() != null) entity.active = dto.getActive();
        if (dto.getProfession() != null) entity.profession = dto.getProfession();
        if (dto.getLieuTravail() != null) entity.lieuTravail = dto.getLieuTravail();
        if (dto.getTelephoneTravail() != null) entity.telephoneTravail = dto.getTelephoneTravail();
        if (dto.getLienParente() != null) entity.lienParente = dto.getLienParente();
        return parentMapper.toDto(parentRepository.persist(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        ParentEntity entity = parentRepository.findById(id);
        if (entity == null) throw new NotFoundException("Parent not found: " + id);
        parentRepository.delete(entity);
    }
}
