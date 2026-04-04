package com.nouraschool.domain.usecases.admin;

import com.nouraschool.domain.dtos.BulletinDto;
import com.nouraschool.domain.dtos.PageDto;
import com.nouraschool.domain.dtos.PageRequest;

import java.util.List;
import java.util.UUID;

public interface AdminBulletinUseCase {

    List<BulletinDto> findAll();

    PageDto<BulletinDto> findAll(PageRequest pageRequest);

    BulletinDto findById(UUID id);

    BulletinDto create(BulletinDto dto);

    BulletinDto update(UUID id, BulletinDto dto);

    void delete(UUID id);
}
