package com.nouraschool.domain.mappers;

import com.nouraschool.domain.dtos.PaiementDto;
import com.nouraschool.domain.entities.PaiementEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;

@ApplicationScoped
public class PaiementMapper {

    private final ModelMapper modelMapper;

    @Inject
    public PaiementMapper(ModelMapperConfig config) {
        this.modelMapper = config.getModelMapper();
        TypeMap<PaiementEntity, PaiementDto> map = modelMapper.createTypeMap(PaiementEntity.class, PaiementDto.class);
        map.addMappings(m -> {
            m.map(src -> src.eleve != null ? src.eleve.id : null, PaiementDto::setEleveId);
            m.map(src -> src.parent != null ? src.parent.id : null, PaiementDto::setParentId);
        });
    }

    public PaiementDto toDto(PaiementEntity entity) {
        return entity == null ? null : modelMapper.map(entity, PaiementDto.class);
    }

    public PaiementEntity toEntity(PaiementDto dto) {
        return dto == null ? null : modelMapper.map(dto, PaiementEntity.class);
    }
}
