package com.nouraschool.domain.usecases.enseignant;

import com.nouraschool.domain.dtos.*;
import com.nouraschool.domain.entities.*;
import com.nouraschool.domain.exception.errors.NotFoundException;
import com.nouraschool.domain.mappers.*;
import com.nouraschool.domain.repositories.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class EnseignantUseCaseImpl implements EnseignantUseCase {

    @Inject EnseignantRepository enseignantRepository;
    @Inject MatiereClasseRepository matiereClasseRepository;
    @Inject EmploiDuTempsRepository emploiDuTempsRepository;
    @Inject NoteRepository noteRepository;
    @Inject BulletinRepository bulletinRepository;
    @Inject AbsenceEnseignantRepository absenceEnseignantRepository;
    @Inject ReclamationRepository reclamationRepository;
    @Inject EleveRepository eleveRepository;
    @Inject MatiereRepository matiereRepository;

    @Inject EnseignantMapper enseignantMapper;
    @Inject MatiereClasseMapper matiereClasseMapper;
    @Inject EmploiDuTempsMapper emploiDuTempsMapper;
    @Inject NoteMapper noteMapper;
    @Inject BulletinMapper bulletinMapper;
    @Inject AbsenceEnseignantMapper absenceEnseignantMapper;
    @Inject ReclamationMapper reclamationMapper;

    @Override
    public EnseignantDto monProfil(UUID enseignantId) {
        var e = enseignantRepository.findById(enseignantId);
        if (e == null) throw new NotFoundException("Enseignant non trouvé");
        return enseignantMapper.toDto(e);
    }

    @Override
    public List<MatiereClasseDto> mesClassesEtMatieres(UUID enseignantId) {
        var list = matiereClasseRepository.findByEnseignantId(enseignantId);
        return list.stream().map(matiereClasseMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<EmploiDuTempsDto> monEmploiDuTemps(UUID enseignantId) {
        var list = emploiDuTempsRepository.findByEnseignantId(enseignantId);
        return list.stream().map(emploiDuTempsMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<NoteDto> notesPourEleveMatiere(UUID enseignantId, UUID eleveId, UUID matiereId) {
        var matiereIds = matiereClasseRepository.findByEnseignantId(enseignantId).stream()
                .map(mc -> mc.matiere.id).distinct().toList();
        if (!matiereIds.contains(matiereId)) return List.of();
        var notes = noteRepository.findByEleveId(eleveId).stream()
                .filter(n -> n.matiere != null && matiereId.equals(n.matiere.id))
                .toList();
        return notes.stream().map(noteMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public NoteDto saisirNote(UUID enseignantId, NoteDto dto) {
        var matiereIds = matiereClasseRepository.findByEnseignantId(enseignantId).stream()
                .map(mc -> mc.matiere.id).toList();
        if (dto.getMatiereId() == null || !matiereIds.contains(dto.getMatiereId()))
            throw new NotFoundException("Matière non assignée à cet enseignant");
        var entity = noteMapper.toEntity(dto);
        entity.eleve = eleveRepository.findById(dto.getEleveId());
        entity.matiere = matiereRepository.findById(dto.getMatiereId());
        if (entity.eleve == null || entity.matiere == null) throw new NotFoundException("Élève ou matière invalide");
        return noteMapper.toDto(noteRepository.persist(entity));
    }

    @Override
    @Transactional
    public NoteDto modifierNote(UUID enseignantId, UUID noteId, NoteDto dto) {
        var entity = noteRepository.findById(noteId);
        if (entity == null) throw new NotFoundException("Note non trouvée");
        var matiereIds = matiereClasseRepository.findByEnseignantId(enseignantId).stream()
                .map(mc -> mc.matiere.id).toList();
        if (!matiereIds.contains(entity.matiere.id)) throw new NotFoundException("Note non modifiable par cet enseignant");
        if (dto.getNote() != null) entity.note = dto.getNote();
        if (dto.getCommentaire() != null) entity.commentaire = dto.getCommentaire();
        return noteMapper.toDto(noteRepository.persist(entity));
    }

    @Override
    public List<BulletinDto> consulterBulletins(UUID enseignantId) {
        return bulletinRepository.findAll().stream().map(bulletinMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AbsenceEnseignantDto declarerAbsence(UUID enseignantId, AbsenceEnseignantDto dto) {
        var enseignant = enseignantRepository.findById(enseignantId);
        if (enseignant == null) throw new NotFoundException("Enseignant non trouvé");
        var entity = absenceEnseignantMapper.toEntity(dto);
        entity.enseignantEntity = enseignant;
        return absenceEnseignantMapper.toDto(absenceEnseignantRepository.persist(entity));
    }

    @Override
    public List<ReclamationDto> reclamationsMesMatieres(UUID enseignantId) {
        var matiereIds = matiereClasseRepository.findByEnseignantId(enseignantId).stream()
                .map(mc -> mc.matiere.id).distinct().toList();
        if (matiereIds.isEmpty()) return List.of();
        return reclamationRepository.findByNoteMatiereIdIn(matiereIds).stream()
                .map(reclamationMapper::toDto).collect(Collectors.toList());
    }
}
