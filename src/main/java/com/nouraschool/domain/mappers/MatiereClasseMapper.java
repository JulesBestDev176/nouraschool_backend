package com.nouraschool.domain.mappers;

import com.nouraschool.domain.dtos.MatiereClasseDto;
import com.nouraschool.domain.entities.MatiereClasseEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;

@ApplicationScoped
public class MatiereClasseMapper {

    private final ModelMapper modelMapper;

    @Inject
    public MatiereClasseMapper(ModelMapperConfig config) {
        this.modelMapper = config.getModelMapper();
        TypeMap<MatiereClasseEntity, MatiereClasseDto> map = modelMapper.createTypeMap(MatiereClasseEntity.class, MatiereClasseDto.class);
        map.addMappings(m -> {
            m.map(src -> src.matiere != null ? src.matiere.id : null, MatiereClasseDto::setMatiereId);
            m.map(src -> src.classe != null ? src.classe.id : null, MatiereClasseDto::setClasseId);
            m.map(src -> src.enseignantEntity != null ? src.enseignantEntity.id : null, MatiereClasseDto::setEnseignantId);
        });
    }

    public MatiereClasseDto toDto(MatiereClasseEntity entity) {
        return entity == null ? null : modelMapper.map(entity, MatiereClasseDto.class);
    }

    public MatiereClasseEntity toEntity(MatiereClasseDto dto) {
        return dto == null ? null : modelMapper.map(dto, MatiereClasseEntity.class);
    }
}
