package com.nouraschool.domain.usecases.eleve;

import com.nouraschool.domain.dtos.*;

import java.util.List;
import java.util.UUID;

public interface EleveUseCase {

    EleveDto monProfil(UUID eleveId);

    List<NoteDto> mesNotes(UUID eleveId);

    List<BulletinDto> mesBulletins(UUID eleveId);

    List<EmploiDuTempsDto> monEmploiDuTemps(UUID eleveId);

    List<AbsenceEleveDto> mesAbsences(UUID eleveId);

    ReclamationDto soumettreReclamation(UUID eleveId, ReclamationDto dto);

    List<NotificationDto> mesNotifications(UUID eleveId);
}
