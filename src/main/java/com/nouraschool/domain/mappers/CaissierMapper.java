package com.nouraschool.domain.mappers;

import com.nouraschool.domain.dtos.CaissierDto;
import com.nouraschool.domain.entities.CaissierEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.modelmapper.ModelMapper;

@ApplicationScoped
public class CaissierMapper {

    private final ModelMapper modelMapper;

    @Inject
    public CaissierMapper(ModelMapperConfig config) {
        this.modelMapper = config.getModelMapper();
    }

    public CaissierDto toDto(CaissierEntity entity) {
        return entity == null ? null : modelMapper.map(entity, CaissierDto.class);
    }
}
