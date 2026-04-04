package com.nouraschool.domain.mappers;

import jakarta.enterprise.context.ApplicationScoped;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import org.modelmapper.config.Configuration.AccessLevel;

@ApplicationScoped
public class ModelMapperConfig {

    private final ModelMapper modelMapper;

    public ModelMapperConfig() {
        this.modelMapper = new ModelMapper();
        this.modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true)
                .setAmbiguityIgnored(true)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(AccessLevel.PUBLIC);
    }

    public ModelMapper getModelMapper() {
        return modelMapper;
    }
}
