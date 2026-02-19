package com.nouraschool.domain.dtos;

import com.nouraschool.domain.enums.LienParente;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParentDto {

    private UUID id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String telephone;
    private String adresse;
    private Boolean active;
    private String profession;
    private String lieuTravail;
    private String telephoneTravail;
    private LienParente lienParente;
}
