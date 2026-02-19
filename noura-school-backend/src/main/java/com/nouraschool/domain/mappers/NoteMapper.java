package com.nouraschool.domain.mappers;

import com.nouraschool.domain.dtos.NoteDto;
import com.nouraschool.domain.entities.NoteEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;

@ApplicationScoped
public class NoteMapper {

    private final ModelMapper modelMapper;

    @Inject
    public NoteMapper(ModelMapperConfig config) {
        this.modelMapper = config.getModelMapper();
        TypeMap<NoteEntity, NoteDto> map = modelMapper.createTypeMap(NoteEntity.class, NoteDto.class);
        map.addMappings(m -> {
            m.map(src -> src.eleve != null ? src.eleve.id : null, NoteDto::setEleveId);
            m.map(src -> src.matiere != null ? src.matiere.id : null, NoteDto::setMatiereId);
        });
    }

    public NoteDto toDto(NoteEntity entity) {
        return entity == null ? null : modelMapper.map(entity, NoteDto.class);
    }

    public NoteEntity toEntity(NoteDto dto) {
        return dto == null ? null : modelMapper.map(dto, NoteEntity.class);
    }
}
