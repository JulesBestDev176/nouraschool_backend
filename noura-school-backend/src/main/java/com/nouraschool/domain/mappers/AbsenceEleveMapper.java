package com.nouraschool.domain.mappers;

import com.nouraschool.domain.dtos.AbsenceEleveDto;
import com.nouraschool.domain.entities.AbsenceEleveEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;

@ApplicationScoped
public class AbsenceEleveMapper {

    private final ModelMapper modelMapper;

    @Inject
    public AbsenceEleveMapper(ModelMapperConfig config) {
        this.modelMapper = config.getModelMapper();
        TypeMap<AbsenceEleveEntity, AbsenceEleveDto> map = modelMapper.createTypeMap(AbsenceEleveEntity.class, AbsenceEleveDto.class);
        map.addMappings(m -> m.map(src -> src.eleve != null ? src.eleve.id : null, AbsenceEleveDto::setEleveId));
    }

    public AbsenceEleveDto toDto(AbsenceEleveEntity entity) {
        return entity == null ? null : modelMapper.map(entity, AbsenceEleveDto.class);
    }

    public AbsenceEleveEntity toEntity(AbsenceEleveDto dto) {
        return dto == null ? null : modelMapper.map(dto, AbsenceEleveEntity.class);
    }
}
