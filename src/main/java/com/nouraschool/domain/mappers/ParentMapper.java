package com.nouraschool.domain.mappers;

import com.nouraschool.domain.dtos.ParentDto;
import com.nouraschool.domain.entities.ParentEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.modelmapper.ModelMapper;

@ApplicationScoped
public class ParentMapper {

    private final ModelMapper modelMapper;

    @Inject
    public ParentMapper(ModelMapperConfig config) {
        this.modelMapper = config.getModelMapper();
    }

    public ParentDto toDto(ParentEntity entity) {
        return entity == null ? null : modelMapper.map(entity, ParentDto.class);
    }
}
