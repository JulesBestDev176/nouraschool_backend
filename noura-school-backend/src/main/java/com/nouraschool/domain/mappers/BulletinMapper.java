package com.nouraschool.domain.mappers;

import com.nouraschool.domain.dtos.BulletinDto;
import com.nouraschool.domain.entities.BulletinEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;

@ApplicationScoped
public class BulletinMapper {

    private final ModelMapper modelMapper;

    @Inject
    public BulletinMapper(ModelMapperConfig config) {
        this.modelMapper = config.getModelMapper();
        TypeMap<BulletinEntity, BulletinDto> map = modelMapper.createTypeMap(BulletinEntity.class, BulletinDto.class);
        map.addMappings(m -> m.map(src -> src.eleve != null ? src.eleve.id : null, BulletinDto::setEleveId));
    }

    public BulletinDto toDto(BulletinEntity entity) {
        return entity == null ? null : modelMapper.map(entity, BulletinDto.class);
    }

    public BulletinEntity toEntity(BulletinDto dto) {
        return dto == null ? null : modelMapper.map(dto, BulletinEntity.class);
    }
}
