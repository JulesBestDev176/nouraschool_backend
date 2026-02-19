package com.nouraschool.domain.usecases.eleve;

import com.nouraschool.domain.dtos.*;
import com.nouraschool.domain.entities.EleveEntity;
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
public class EleveUseCaseImpl implements EleveUseCase {

    @Inject EleveRepository eleveRepository;
    @Inject NoteRepository noteRepository;
    @Inject BulletinRepository bulletinRepository;
    @Inject EmploiDuTempsRepository emploiDuTempsRepository;
    @Inject AbsenceEleveRepository absenceEleveRepository;
    @Inject ReclamationRepository reclamationRepository;
    @Inject NotificationRepository notificationRepository;

    @Inject EleveMapper eleveMapper;
    @Inject NoteMapper noteMapper;
    @Inject BulletinMapper bulletinMapper;
    @Inject EmploiDuTempsMapper emploiDuTempsMapper;
    @Inject AbsenceEleveMapper absenceEleveMapper;
    @Inject ReclamationMapper reclamationMapper;
    @Inject NotificationMapper notificationMapper;

    @Override
    public EleveDto monProfil(UUID eleveId) {
        var e = eleveRepository.findById(eleveId);
        if (e == null) throw new NotFoundException("Élève non trouvé");
        return eleveMapper.toDto(e);
    }

    @Override
    public List<NoteDto> mesNotes(UUID eleveId) {
        return noteRepository.findByEleveId(eleveId).stream().map(noteMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<BulletinDto> mesBulletins(UUID eleveId) {
        return bulletinRepository.findByEleveId(eleveId).stream().map(bulletinMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<EmploiDuTempsDto> monEmploiDuTemps(UUID eleveId) {
        var eleve = eleveRepository.findById(eleveId);
        if (eleve == null || eleve.classe == null) return List.of();
        return emploiDuTempsRepository.findByClasseId(eleve.classe.id).stream()
                .map(emploiDuTempsMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<AbsenceEleveDto> mesAbsences(UUID eleveId) {
        return absenceEleveRepository.findByEleveId(eleveId).stream()
                .map(absenceEleveMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReclamationDto soumettreReclamation(UUID eleveId, ReclamationDto dto) {
        var eleve = eleveRepository.findById(eleveId);
        if (eleve == null) throw new NotFoundException("Élève non trouvé");
        var entity = reclamationMapper.toEntity(dto);
        entity.eleve = eleve;
        return reclamationMapper.toDto(reclamationRepository.persist(entity));
    }

    @Override
    public List<NotificationDto> mesNotifications(UUID eleveId) {
        return notificationRepository.findByUserId(eleveId).stream()
                .map(notificationMapper::toDto).collect(Collectors.toList());
    }
}
