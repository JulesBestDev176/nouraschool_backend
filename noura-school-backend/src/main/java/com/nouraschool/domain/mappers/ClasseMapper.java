package com.nouraschool.domain.mappers;

import com.nouraschool.domain.dtos.ClasseDto;
import com.nouraschool.domain.entities.ClasseEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.modelmapper.ModelMapper;

@ApplicationScoped
public class ClasseMapper {

    private final ModelMapper modelMapper;

    @Inject
    public ClasseMapper(ModelMapperConfig config) {
        this.modelMapper = config.getModelMapper();
    }

    public ClasseDto toDto(ClasseEntity entity) {
        return entity == null ? null : modelMapper.map(entity, ClasseDto.class);
    }

    public ClasseEntity toEntity(ClasseDto dto) {
        return dto == null ? null : modelMapper.map(dto, ClasseEntity.class);
    }
}
