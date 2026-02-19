package com.nouraschool.domain.mappers;

import com.nouraschool.domain.dtos.UserDto;
import com.nouraschool.domain.entities.UserEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.modelmapper.ModelMapper;

@ApplicationScoped
public class UserMapper {

    private final ModelMapper modelMapper;

    @Inject
    public UserMapper(ModelMapperConfig config) {
        this.modelMapper = config.getModelMapper();
        this.modelMapper.typeMap(UserEntity.class, UserDto.class).addMappings(m -> m.skip(UserDto::setId));
    }

    public UserDto toDto(UserEntity entity) {
        if (entity == null) return null;
        UserDto dto = modelMapper.map(entity, UserDto.class);
        dto.setId(entity.id);
        return dto;
    }
}
