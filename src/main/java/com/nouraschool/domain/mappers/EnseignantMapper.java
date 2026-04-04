package com.nouraschool.domain.mappers;

import com.nouraschool.domain.dtos.EnseignantDto;
import com.nouraschool.domain.entities.EnseignantEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.modelmapper.ModelMapper;

@ApplicationScoped
public class EnseignantMapper {

    private final ModelMapper modelMapper;

    @Inject
    public EnseignantMapper(ModelMapperConfig config) {
        this.modelMapper = config.getModelMapper();
    }

    public EnseignantDto toDto(EnseignantEntity entity) {
        return entity == null ? null : modelMapper.map(entity, EnseignantDto.class);
    }
}
