package com.nouraschool.domain.usecases.admin;

import com.nouraschool.domain.dtos.UserCreateDto;
import com.nouraschool.domain.dtos.UserDto;
import com.nouraschool.domain.entities.AdministrateurEntity;
import com.nouraschool.domain.entities.CaissierEntity;
import com.nouraschool.domain.entities.SurveillantEntity;
import com.nouraschool.domain.entities.UserEntity;
import com.nouraschool.domain.enums.UserRole;
import com.nouraschool.domain.exception.errors.InvalidRequestException;
import com.nouraschool.domain.exception.errors.NotFoundException;
import com.nouraschool.domain.mappers.UserMapper;
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
public class AdminUserUseCaseImpl implements AdminUserUseCase {

    @Inject
    UserRepository userRepository;
    @Inject
    TenantRepository tenantRepository;
    @Inject
    UserMapper userMapper;
    @Inject
    PasswordEncoder passwordEncoder;

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public UserDto findById(UUID id) {
        UserEntity entity = userRepository.findById(id);
        if (entity == null) throw new NotFoundException("User not found: " + id);
        return userMapper.toDto(entity);
    }

    @Override
    @Transactional
    public UserDto create(UserCreateDto dto) {
        if (dto.getRole() != UserRole.ADMIN && dto.getRole() != UserRole.CAISSIER && dto.getRole() != UserRole.SURVEILLANT) {
            throw new InvalidRequestException("Only ADMIN, CAISSIER or SURVEILLANT can be created via this endpoint");
        }
        if (userRepository.findByUsername(dto.getUsername()) != null) throw new InvalidRequestException("EMAIL_DEJA_UTILISE");
        if (userRepository.findByUsernameOrEmail(dto.getEmail()) != null) throw new InvalidRequestException("EMAIL_DEJA_UTILISE");

        UserEntity entity;
        if (dto.getRole() == UserRole.ADMIN) {
            AdministrateurEntity admin = new AdministrateurEntity();
            fillUserFields(admin, dto);
            admin.role = UserRole.ADMIN;
            entity = userRepository.persist(admin);
        } else if (dto.getRole() == UserRole.CAISSIER) {
            CaissierEntity caissier = new CaissierEntity();
            fillUserFields(caissier, dto);
            caissier.role = UserRole.CAISSIER;
            entity = userRepository.persist(caissier);
        } else {
            SurveillantEntity surveillant = new SurveillantEntity();
            fillUserFields(surveillant, dto);
            surveillant.role = UserRole.SURVEILLANT;
            entity = userRepository.persist(surveillant);
        }
        return userMapper.toDto(entity);
    }

    private void fillUserFields(UserEntity entity, UserCreateDto dto) {
        entity.tenant = tenantRepository.findDefault();
        entity.username = dto.getUsername();
        entity.email = dto.getEmail();
        entity.password = passwordEncoder.encode(dto.getPassword());
        entity.firstName = dto.getFirstName();
        entity.lastName = dto.getLastName();
        entity.telephone = dto.getTelephone();
        entity.adresse = dto.getAdresse();
        entity.mustChangePassword = true;
    }

    @Override
    @Transactional
    public UserDto update(UUID id, UserDto dto) {
        UserEntity entity = userRepository.findById(id);
        if (entity == null) throw new NotFoundException("User not found: " + id);
        if (dto.getUsername() != null) entity.username = dto.getUsername();
        if (dto.getEmail() != null) entity.email = dto.getEmail();
        if (dto.getFirstName() != null) entity.firstName = dto.getFirstName();
        if (dto.getLastName() != null) entity.lastName = dto.getLastName();
        if (dto.getTelephone() != null) entity.telephone = dto.getTelephone();
        if (dto.getAdresse() != null) entity.adresse = dto.getAdresse();
        if (dto.getActive() != null) {
            if (Boolean.FALSE.equals(dto.getActive()) && entity.role == UserRole.ADMIN && entity.tenant != null && Boolean.TRUE.equals(entity.active)) {
                long adminCount = UserEntity.count("tenant.id = ?1 and role = ?2 and active = true", entity.tenant.id, UserRole.ADMIN);
                if (adminCount <= 1) {
                    throw new InvalidRequestException("REGLE_METIER_VIOLEE");
                }
            }
            entity.active = dto.getActive();
        }
        return userMapper.toDto(userRepository.persist(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        UserEntity entity = userRepository.findById(id);
        if (entity == null) throw new NotFoundException("User not found: " + id);
        userRepository.delete(entity);
    }
}
