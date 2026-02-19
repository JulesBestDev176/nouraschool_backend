package com.nouraschool.domain.usecases.parent;

import com.nouraschool.domain.dtos.*;
import com.nouraschool.domain.entities.ParentEntity;
import com.nouraschool.domain.exception.errors.NotFoundException;
import com.nouraschool.domain.mappers.*;
import com.nouraschool.domain.repositories.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class ParentUseCaseImpl implements ParentUseCase {

    @Inject ParentRepository parentRepository;
    @Inject NoteRepository noteRepository;
    @Inject BulletinRepository bulletinRepository;
    @Inject AbsenceEleveRepository absenceEleveRepository;
    @Inject EmploiDuTempsRepository emploiDuTempsRepository;
    @Inject PaiementRepository paiementRepository;
    @Inject NotificationRepository notificationRepository;

    @Inject ParentMapper parentMapper;
    @Inject EleveMapper eleveMapper;
    @Inject NoteMapper noteMapper;
    @Inject BulletinMapper bulletinMapper;
    @Inject AbsenceEleveMapper absenceEleveMapper;
    @Inject EmploiDuTempsMapper emploiDuTempsMapper;
    @Inject PaiementMapper paiementMapper;
    @Inject NotificationMapper notificationMapper;

    @Override
    public ParentDto monProfil(UUID parentId) {
        var p = parentRepository.findById(parentId);
        if (p == null) throw new NotFoundException("Parent non trouvé");
        return parentMapper.toDto(p);
    }

    @Override
    public List<EleveDto> mesEnfants(UUID parentId) {
        var parent = parentRepository.findById(parentId);
        if (parent == null || parent.enfants == null) return List.of();
        return parent.enfants.stream().map(eleveMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public EleveDto profilEnfant(UUID parentId, UUID eleveId) {
        var parent = parentRepository.findById(parentId);
        if (parent == null || parent.enfants == null) throw new NotFoundException("Parent non trouvé");
        var enfant = parent.enfants.stream().filter(e -> e.id.equals(eleveId)).findFirst().orElse(null);
        if (enfant == null) throw new NotFoundException("Enfant non trouvé ou non lié à ce parent");
        return eleveMapper.toDto(enfant);
    }

    @Override
    public List<NoteDto> notesEnfant(UUID parentId, UUID eleveId) {
        verifierParentEnfant(parentId, eleveId);
        return noteRepository.findByEleveId(eleveId).stream().map(noteMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<BulletinDto> bulletinsEnfant(UUID parentId, UUID eleveId) {
        verifierParentEnfant(parentId, eleveId);
        return bulletinRepository.findByEleveId(eleveId).stream().map(bulletinMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<AbsenceEleveDto> absencesEnfant(UUID parentId, UUID eleveId) {
        verifierParentEnfant(parentId, eleveId);
        return absenceEleveRepository.findByEleveId(eleveId).stream()
                .map(absenceEleveMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<EmploiDuTempsDto> emploiDuTempsEnfant(UUID parentId, UUID eleveId) {
        verifierParentEnfant(parentId, eleveId);
        var parent = parentRepository.findById(parentId);
        var eleve = parent.enfants.stream().filter(e -> e.id.equals(eleveId)).findFirst().orElse(null);
        if (eleve == null || eleve.classe == null) return List.of();
        return emploiDuTempsRepository.findByClasseId(eleve.classe.id).stream()
                .map(emploiDuTempsMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<PaiementDto> historiquePaiements(UUID parentId) {
        return paiementRepository.findByParentId(parentId).stream()
                .map(paiementMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<NotificationDto> mesNotifications(UUID parentId) {
        return notificationRepository.findByUserId(parentId).stream()
                .map(notificationMapper::toDto).collect(Collectors.toList());
    }

    private void verifierParentEnfant(UUID parentId, UUID eleveId) {
        var parent = parentRepository.findById(parentId);
        if (parent == null || parent.enfants == null) throw new NotFoundException("Parent non trouvé");
        boolean found = parent.enfants.stream().anyMatch(e -> e.id.equals(eleveId));
        if (!found) throw new NotFoundException("Enfant non trouvé ou non lié à ce parent");
    }
}
