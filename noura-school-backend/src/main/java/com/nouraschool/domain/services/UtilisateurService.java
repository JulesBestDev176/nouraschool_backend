package com.nouraschool.domain.services;

import com.nouraschool.domain.dtos.UserCreateDto;
import com.nouraschool.domain.dtos.UserDto;

import java.util.List;
import java.util.UUID;

public interface UtilisateurService {

    List<UserDto> findAll();

    UserDto findById(UUID id);

    UserDto create(UserCreateDto dto);

    UserDto update(UUID id, UserDto dto);

    void delete(UUID id);

    void reinitialiserMdp(UUID userId, String newPassword);

    void assignerCycles(UUID userId, List<UUID> cycleIds);

    void retirerCycle(UUID userId, UUID cycleId);
}
