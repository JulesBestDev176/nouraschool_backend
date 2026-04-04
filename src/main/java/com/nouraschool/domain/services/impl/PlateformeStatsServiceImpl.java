package com.nouraschool.domain.services.impl;

import com.nouraschool.domain.dtos.platform.PlateformeStatsDto;
import com.nouraschool.domain.entities.PlateformeUtilisateurEntity;
import com.nouraschool.domain.entities.TenantEntity;
import com.nouraschool.domain.entities.UserEntity;
import com.nouraschool.domain.services.PlateformeStatsService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PlateformeStatsServiceImpl implements PlateformeStatsService {

    @Override
    public PlateformeStatsDto getStats() {
        long nbTenants = TenantEntity.count();
        long nbTenantsActifs = TenantEntity.count("actif", true);
        long nbPlateformeUtilisateurs = PlateformeUtilisateurEntity.count();
        long nbUtilisateurs = UserEntity.count();
        return PlateformeStatsDto.builder()
                .nbTenants(nbTenants)
                .nbTenantsActifs(nbTenantsActifs)
                .nbPlateformeUtilisateurs(nbPlateformeUtilisateurs)
                .nbUtilisateurs(nbUtilisateurs)
                .build();
    }
}
