package com.nouraschool.domain.mappers;

import com.nouraschool.domain.dtos.EmploiDuTempsDto;
import com.nouraschool.domain.entities.EmploiDuTempsEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;

@ApplicationScoped
public class EmploiDuTempsMapper {

    private final ModelMapper modelMapper;

    @Inject
    public EmploiDuTempsMapper(ModelMapperConfig config) {
        this.modelMapper = config.getModelMapper();
        TypeMap<EmploiDuTempsEntity, EmploiDuTempsDto> map = modelMapper.createTypeMap(EmploiDuTempsEntity.class, EmploiDuTempsDto.class);
        map.addMappings(m -> {
            m.map(src -> src.classe != null ? src.classe.id : null, EmploiDuTempsDto::setClasseId);
            m.map(src -> src.matiere != null ? src.matiere.id : null, EmploiDuTempsDto::setMatiereId);
            m.map(src -> src.enseignantEntity != null ? src.enseignantEntity.id : null, EmploiDuTempsDto::setEnseignantId);
        });
    }

    public EmploiDuTempsDto toDto(EmploiDuTempsEntity entity) {
        return entity == null ? null : modelMapper.map(entity, EmploiDuTempsDto.class);
    }

    public EmploiDuTempsEntity toEntity(EmploiDuTempsDto dto) {
        return dto == null ? null : modelMapper.map(dto, EmploiDuTempsEntity.class);
    }
}
