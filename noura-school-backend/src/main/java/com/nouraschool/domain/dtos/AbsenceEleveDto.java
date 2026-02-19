package com.nouraschool.domain.dtos;

import com.nouraschool.domain.enums.TypeAbsence;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AbsenceEleveDto {

    private UUID id;
    private UUID eleveId;
    private LocalDate date;
    private TypeAbsence typeAbsence;
    private Boolean justifiee;
    private String motif;
    private String documentJustificatifUrl;
    private UUID declaredBy;
    private LocalDateTime createdAt;
}
