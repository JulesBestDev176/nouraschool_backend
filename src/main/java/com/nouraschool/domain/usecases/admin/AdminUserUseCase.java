package com.nouraschool.domain.usecases.admin;

import com.nouraschool.domain.dtos.UserCreateDto;
import com.nouraschool.domain.dtos.UserDto;

import java.util.List;
import java.util.UUID;

public interface AdminUserUseCase {

    List<UserDto> findAll();

    UserDto findById(UUID id);

    UserDto create(UserCreateDto dto);

    UserDto update(UUID id, UserDto dto);

    void delete(UUID id);
}
