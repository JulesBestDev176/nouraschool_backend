package com.nouraschool.domain.repositories;

import com.nouraschool.domain.entities.SurveillantCycleEntity;

import java.util.List;
import java.util.UUID;

public interface SurveillantCycleRepository {

    List<SurveillantCycleEntity> findBySurveillantId(UUID surveillantId);

    /** Codes des cycles assignés (ex. COLLEGE, LYCEE). */
    List<String> findCycleCodesBySurveillantId(UUID surveillantId);

    boolean exists(UUID surveillantId, UUID cycleId);

    SurveillantCycleEntity persist(SurveillantCycleEntity entity);

    void delete(UUID surveillantId, UUID cycleId);
}
