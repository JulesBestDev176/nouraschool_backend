package com.nouraschool.domain.mappers;

import com.nouraschool.domain.dtos.AdministrateurDto;
import com.nouraschool.domain.entities.AdministrateurEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.modelmapper.ModelMapper;

@ApplicationScoped
public class AdministrateurMapper {

    private final ModelMapper modelMapper;

    @Inject
    public AdministrateurMapper(ModelMapperConfig config) {
        this.modelMapper = config.getModelMapper();
    }

    public AdministrateurDto toDto(AdministrateurEntity entity) {
        return entity == null ? null : modelMapper.map(entity, AdministrateurDto.class);
    }
}
