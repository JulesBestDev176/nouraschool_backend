package com.nouraschool.domain.mappers;

import com.nouraschool.domain.dtos.MatiereDto;
import com.nouraschool.domain.entities.MatiereEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.modelmapper.ModelMapper;

@ApplicationScoped
public class MatiereMapper {

    private final ModelMapper modelMapper;

    @Inject
    public MatiereMapper(ModelMapperConfig config) {
        this.modelMapper = config.getModelMapper();
    }

    public MatiereDto toDto(MatiereEntity entity) {
        return entity == null ? null : modelMapper.map(entity, MatiereDto.class);
    }

    public MatiereEntity toEntity(MatiereDto dto) {
        return dto == null ? null : modelMapper.map(dto, MatiereEntity.class);
    }
}
