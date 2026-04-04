package com.nouraschool.domain.dtos.platform;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlateformeStatsDto {
    private long nbTenants;
    private long nbTenantsActifs;
    private long nbPlateformeUtilisateurs;
    private long nbUtilisateurs;
}
