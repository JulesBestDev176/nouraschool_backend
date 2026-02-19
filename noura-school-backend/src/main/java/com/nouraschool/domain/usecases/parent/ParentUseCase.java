package com.nouraschool.domain.usecases.parent;

import com.nouraschool.domain.dtos.*;

import java.util.List;
import java.util.UUID;

/**
 * Use case Parent : profil enfant, notes, bulletins, absences, EDT, paiements, notifications.
 */
public interface ParentUseCase {

    ParentDto monProfil(UUID parentId);

    List<EleveDto> mesEnfants(UUID parentId);

    EleveDto profilEnfant(UUID parentId, UUID eleveId);

    List<NoteDto> notesEnfant(UUID parentId, UUID eleveId);

    List<BulletinDto> bulletinsEnfant(UUID parentId, UUID eleveId);

    List<AbsenceEleveDto> absencesEnfant(UUID parentId, UUID eleveId);

    List<EmploiDuTempsDto> emploiDuTempsEnfant(UUID parentId, UUID eleveId);

    List<PaiementDto> historiquePaiements(UUID parentId);

    List<NotificationDto> mesNotifications(UUID parentId);
}
