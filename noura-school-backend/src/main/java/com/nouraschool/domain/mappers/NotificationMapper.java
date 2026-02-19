package com.nouraschool.domain.mappers;

import com.nouraschool.domain.dtos.NotificationDto;
import com.nouraschool.domain.entities.NotificationEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.modelmapper.ModelMapper;

@ApplicationScoped
public class NotificationMapper {

    private final ModelMapper modelMapper;

    @Inject
    public NotificationMapper(ModelMapperConfig config) {
        this.modelMapper = config.getModelMapper();
    }

    public NotificationDto toDto(NotificationEntity entity) {
        return entity == null ? null : modelMapper.map(entity, NotificationDto.class);
    }

    public NotificationEntity toEntity(NotificationDto dto) {
        return dto == null ? null : modelMapper.map(dto, NotificationEntity.class);
    }
}
