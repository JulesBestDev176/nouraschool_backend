package com.nouraschool.domain.mappers;

import jakarta.enterprise.context.ApplicationScoped;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

@ApplicationScoped
public class ModelMapperConfig {

    private final ModelMapper modelMapper;

    public ModelMapperConfig() {
        this.modelMapper = new ModelMapper();
        this.modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true)
                .setAmbiguityIgnored(true);
    }

    public ModelMapper getModelMapper() {
        return modelMapper;
    }
}
