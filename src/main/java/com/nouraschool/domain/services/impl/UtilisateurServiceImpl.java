package com.nouraschool.domain.services.impl;

import com.nouraschool.domain.dtos.UserCreateDto;
import com.nouraschool.domain.dtos.UserDto;
import com.nouraschool.domain.entities.SurveillantCycleEntity;
import com.nouraschool.domain.entities.UserEntity;
import com.nouraschool.domain.enums.UserRole;
import com.nouraschool.domain.exception.errors.InvalidRequestException;
import com.nouraschool.domain.exception.errors.NotFoundException;
import com.nouraschool.domain.mappers.UserMapper;
import com.nouraschool.domain.repositories.CycleRepository;
import com.nouraschool.domain.repositories.RefreshTokenRepository;
import com.nouraschool.domain.repositories.SurveillantCycleRepository;
import com.nouraschool.domain.constants.Constants;
import com.nouraschool.domain.repositories.UserRepository;
import com.nouraschool.domain.services.PasswordEncoder;
import com.nouraschool.domain.services.UtilisateurService;
import com.nouraschool.domain.usecases.admin.AdminUserUseCase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UtilisateurServiceImpl implements UtilisateurService {

    @Inject
    AdminUserUseCase adminUserUseCase;

    @Inject
    UserRepository userRepository;

    @Inject
    CycleRepository cycleRepository;

    @Inject
    SurveillantCycleRepository surveillantCycleRepository;

    @Inject
    RefreshTokenRepository refreshTokenRepository;

    @Inject
    PasswordEncoder passwordEncoder;

    @Inject
    UserMapper userMapper;


    @Override
    public List<UserDto> findAll() {
        return adminUserUseCase.findAll();
    }

    @Override
    public UserDto findById(UUID id) {
        return adminUserUseCase.findById(id);
    }

    @Override
    public UserDto create(UserCreateDto dto) {
        return adminUserUseCase.create(dto);
    }

    @Override
    public UserDto update(UUID id, UserDto dto) {
        return adminUserUseCase.update(id, dto);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        UserEntity user = userRepository.findById(id);
        if (user == null) throw new NotFoundException("RESSOURCE_INTROUVABLE");
        if (user.role == UserRole.ADMIN && user.tenant != null) {
            long adminCount = UserEntity.count("tenant.id = ?1 and role = ?2", user.tenant.id, UserRole.ADMIN);
            if (adminCount <= 1) {
                throw new InvalidRequestException("REGLE_METIER_VIOLEE");
            }
        }
        adminUserUseCase.delete(id);
    }

    @Override
    @Transactional
    public void reinitialiserMdp(UUID userId, String newPassword) {
        UserEntity user = userRepository.findById(userId);
        if (user == null) throw new NotFoundException("RESSOURCE_INTROUVABLE");
        if (newPassword == null || newPassword.length() < Constants.PASSWORD_MIN_LENGTH) {
            throw new InvalidRequestException("MOT_DE_PASSE_TROP_COURT");
        }
        user.password = passwordEncoder.encode(newPassword);
        user.mustChangePassword = true;
        userRepository.persist(user);
        refreshTokenRepository.revokeByUserId(userId);
    }

    @Override
    @Transactional
    public void assignerCycles(UUID userId, List<UUID> cycleIds) {
        UserEntity user = userRepository.findById(userId);
        if (user == null) throw new NotFoundException("RESSOURCE_INTROUVABLE");
        if (user.role != UserRole.SURVEILLANT) {
            throw new InvalidRequestException("REGLE_METIER_VIOLEE");
        }
        if (cycleIds == null) return;
        for (UUID cycleId : cycleIds) {
            if (cycleRepository.findById(cycleId) == null) continue;
            if (surveillantCycleRepository.exists(userId, cycleId)) continue;
            SurveillantCycleEntity sc = new SurveillantCycleEntity();
            sc.surveillantId = userId;
            sc.cycleId = cycleId;
            surveillantCycleRepository.persist(sc);
        }
    }

    @Override
    @Transactional
    public void retirerCycle(UUID userId, UUID cycleId) {
        UserEntity user = userRepository.findById(userId);
        if (user == null) throw new NotFoundException("RESSOURCE_INTROUVABLE");
        surveillantCycleRepository.delete(userId, cycleId);
    }
}
