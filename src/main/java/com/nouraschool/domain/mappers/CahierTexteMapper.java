package com.nouraschool.domain.mappers;

import com.nouraschool.domain.dtos.CahierTexteDto;
import com.nouraschool.domain.entities.CahierTexteEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.modelmapper.ModelMapper;

@ApplicationScoped
public class CahierTexteMapper {

    private final ModelMapper modelMapper;

    @Inject
    public CahierTexteMapper(ModelMapperConfig config) {
        this.modelMapper = config.getModelMapper();
    }

    public CahierTexteDto toDto(CahierTexteEntity entity) {
        return entity == null ? null : modelMapper.map(entity, CahierTexteDto.class);
    }

    public CahierTexteEntity toEntity(CahierTexteDto dto) {
        return dto == null ? null : modelMapper.map(dto, CahierTexteEntity.class);
    }
}
