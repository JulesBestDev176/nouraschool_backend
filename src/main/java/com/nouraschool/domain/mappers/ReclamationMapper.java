package com.nouraschool.domain.mappers;

import com.nouraschool.domain.dtos.ReclamationDto;
import com.nouraschool.domain.entities.ReclamationEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;

@ApplicationScoped
public class ReclamationMapper {

    private final ModelMapper modelMapper;

    @Inject
    public ReclamationMapper(ModelMapperConfig config) {
        this.modelMapper = config.getModelMapper();
        TypeMap<ReclamationEntity, ReclamationDto> map = modelMapper.createTypeMap(ReclamationEntity.class, ReclamationDto.class);
        map.addMappings(m -> {
            m.map(src -> src.eleve != null ? src.eleve.id : null, ReclamationDto::setEleveId);
            m.map(src -> src.note != null ? src.note.id : null, ReclamationDto::setNoteId);
            m.map(src -> src.absence != null ? src.absence.id : null, ReclamationDto::setAbsenceId);
        });
    }

    public ReclamationDto toDto(ReclamationEntity entity) {
        return entity == null ? null : modelMapper.map(entity, ReclamationDto.class);
    }

    public ReclamationEntity toEntity(ReclamationDto dto) {
        return dto == null ? null : modelMapper.map(dto, ReclamationEntity.class);
    }
}
