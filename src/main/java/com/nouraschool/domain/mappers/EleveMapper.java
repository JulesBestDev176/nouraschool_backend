package com.nouraschool.domain.mappers;

import com.nouraschool.domain.dtos.EleveDto;
import com.nouraschool.domain.entities.EleveEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;

import java.util.Collections;
import java.util.stream.Collectors;

@ApplicationScoped
public class EleveMapper {

    private final ModelMapper modelMapper;

    @Inject
    public EleveMapper(ModelMapperConfig config) {
        this.modelMapper = config.getModelMapper();
        TypeMap<EleveEntity, EleveDto> map = modelMapper.createTypeMap(EleveEntity.class, EleveDto.class);
        map.addMappings(m -> {
            m.map(src -> src.classe != null ? src.classe.id : null, EleveDto::setClasseId);
            m.map(src -> src.parents != null ? src.parents.stream().map(p -> p.id).collect(Collectors.toList()) : Collections.emptyList(), EleveDto::setParentIds);
        });
    }

    public EleveDto toDto(EleveEntity entity) {
        return entity == null ? null : modelMapper.map(entity, EleveDto.class);
    }
}
