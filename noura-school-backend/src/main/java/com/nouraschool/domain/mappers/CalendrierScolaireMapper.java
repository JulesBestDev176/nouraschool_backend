package com.nouraschool.domain.mappers;

import com.nouraschool.domain.dtos.CalendrierScolaireDto;
import com.nouraschool.domain.entities.CalendrierScolaireEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.modelmapper.ModelMapper;

@ApplicationScoped
public class CalendrierScolaireMapper {

    private final ModelMapper modelMapper;

    @Inject
    public CalendrierScolaireMapper(ModelMapperConfig config) {
        this.modelMapper = config.getModelMapper();
    }

    public CalendrierScolaireDto toDto(CalendrierScolaireEntity entity) {
        return entity == null ? null : modelMapper.map(entity, CalendrierScolaireDto.class);
    }

    public CalendrierScolaireEntity toEntity(CalendrierScolaireDto dto) {
        return dto == null ? null : modelMapper.map(dto, CalendrierScolaireEntity.class);
    }
}
