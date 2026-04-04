package com.nouraschool.domain.mappers;

import com.nouraschool.domain.dtos.AbsenceEnseignantDto;
import com.nouraschool.domain.entities.AbsenceEnseignantEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;

@ApplicationScoped
public class AbsenceEnseignantMapper {

    private final ModelMapper modelMapper;

    @Inject
    public AbsenceEnseignantMapper(ModelMapperConfig config) {
        this.modelMapper = config.getModelMapper();
        TypeMap<AbsenceEnseignantEntity, AbsenceEnseignantDto> map = modelMapper.createTypeMap(AbsenceEnseignantEntity.class, AbsenceEnseignantDto.class);
        map.addMappings(m -> m.map(src -> src.enseignantEntity != null ? src.enseignantEntity.id : null, AbsenceEnseignantDto::setEnseignantId));
    }

    public AbsenceEnseignantDto toDto(AbsenceEnseignantEntity entity) {
        return entity == null ? null : modelMapper.map(entity, AbsenceEnseignantDto.class);
    }

    public AbsenceEnseignantEntity toEntity(AbsenceEnseignantDto dto) {
        return dto == null ? null : modelMapper.map(dto, AbsenceEnseignantEntity.class);
    }
}
