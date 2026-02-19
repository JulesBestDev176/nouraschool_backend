package com.nouraschool.domain.usecases.enseignant;

import com.nouraschool.domain.dtos.*;

import java.util.List;
import java.util.UUID;

/**
 * Use case Enseignant : classes/matières, notes, bulletins, absences, réclamations.
 */
public interface EnseignantUseCase {

    EnseignantDto monProfil(UUID enseignantId);

    List<MatiereClasseDto> mesClassesEtMatieres(UUID enseignantId);

    List<EmploiDuTempsDto> monEmploiDuTemps(UUID enseignantId);

    List<NoteDto> notesPourEleveMatiere(UUID enseignantId, UUID eleveId, UUID matiereId);

    NoteDto saisirNote(UUID enseignantId, NoteDto dto);

    NoteDto modifierNote(UUID enseignantId, UUID noteId, NoteDto dto);

    List<BulletinDto> consulterBulletins(UUID enseignantId);

    AbsenceEnseignantDto declarerAbsence(UUID enseignantId, AbsenceEnseignantDto dto);

    List<ReclamationDto> reclamationsMesMatieres(UUID enseignantId);
}
