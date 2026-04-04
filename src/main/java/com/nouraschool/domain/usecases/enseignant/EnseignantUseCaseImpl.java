package com.nouraschool.domain.usecases.enseignant;

import com.nouraschool.domain.dtos.*;
import com.nouraschool.domain.entities.*;
import com.nouraschool.domain.enums.TypeAbsence;
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
    @Inject AppelRepository appelRepository;
    @Inject AppelLigneRepository appelLigneRepository;
    @Inject CahierTexteRepository cahierTexteRepository;
    @Inject AbsenceEleveRepository absenceEleveRepository;
    @Inject CoursRepository coursRepository;
    @Inject com.nouraschool.runtime.tenant.TenantContext tenantContext;

    @Inject EnseignantMapper enseignantMapper;
    @Inject MatiereClasseMapper matiereClasseMapper;
    @Inject EmploiDuTempsMapper emploiDuTempsMapper;
    @Inject NoteMapper noteMapper;
    @Inject BulletinMapper bulletinMapper;
    @Inject AbsenceEnseignantMapper absenceEnseignantMapper;
    @Inject ReclamationMapper reclamationMapper;
    @Inject AppelMapper appelMapper;
    @Inject CahierTexteMapper cahierTexteMapper;

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

    @Override
    @Transactional
    public AppelDto creerAppel(UUID enseignantId, AppelDto dto) {
        var cours = coursRepository.findById(dto.getCoursId());
        if (cours == null) throw new NotFoundException("Cours non trouvé");
        if (!enseignantId.equals(cours.professeurId))
            throw new NotFoundException("Ce cours n'est pas assigné à cet enseignant");
        var tenantId = tenantContext.getTenantId();
        if (tenantId == null) throw new NotFoundException("Tenant non défini");

        var entity = new AppelEntity();
        entity.tenantId = tenantId;
        entity.coursId = dto.getCoursId();
        entity.dateCours = dto.getDateCours();
        entity.heureDebut = dto.getHeureDebut();
        entity.statut = "BROUILLON";
        entity.soumisPar = enseignantId;

        appelRepository.persist(entity);

        if (dto.getLignes() != null) {
            for (var ligneDto : dto.getLignes()) {
                var ligne = appelMapper.toLigneEntity(ligneDto, entity.id);
                if (ligne != null) {
                    ligne.appel = entity;
                    appelLigneRepository.persist(ligne);
                }
            }
        }
        entity = appelRepository.findById(entity.id);
        return appelMapper.toDto(entity);
    }

    @Override
    public List<AppelDto> listeAppels(UUID enseignantId, UUID coursId) {
        var coursIds = coursRepository.findByProfesseurId(enseignantId).stream()
                .map(c -> c.id).toList();
        if (coursId != null && !coursIds.contains(coursId)) return List.of();
        var ids = coursId != null ? List.of(coursId) : coursIds;
        if (ids.isEmpty()) return List.of();
        return ids.stream()
                .flatMap(id -> appelRepository.findByCoursId(id).stream())
                .map(appelMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AppelDto soumettreAppel(UUID enseignantId, UUID appelId) {
        var entity = appelRepository.findById(appelId);
        if (entity == null) throw new NotFoundException("Appel non trouvé");
        if (!enseignantId.equals(entity.soumisPar))
            throw new NotFoundException("Cet appel n'appartient pas à cet enseignant");
        if (!"BROUILLON".equals(entity.statut))
            throw new NotFoundException("L'appel est déjà soumis");

        entity.statut = "EN_ATTENTE";
        appelRepository.persist(entity);

        var lignes = appelLigneRepository.findByAppelId(appelId);
        for (var ligne : lignes) {
            if ("ABSENT".equals(ligne.statut)) {
                var abs = new AbsenceEleveEntity();
                abs.date = entity.dateCours;
                abs.typeAbsence = TypeAbsence.COURS_SPECIFIQUE;
                abs.justifiee = false;
                abs.declaredBy = entity.soumisPar;
                abs.statut = "EN_ATTENTE";
                abs.eleve = eleveRepository.findById(ligne.eleveId);
                if (abs.eleve != null) absenceEleveRepository.persist(abs);
            }
        }
        entity = appelRepository.findById(appelId);
        return appelMapper.toDto(entity);
    }

    @Override
    @Transactional
    public CahierTexteDto creerCahierTexte(UUID enseignantId, CahierTexteDto dto) {
        var cours = coursRepository.findById(dto.getCoursId());
        if (cours == null) throw new NotFoundException("Cours non trouvé");
        if (!enseignantId.equals(cours.professeurId))
            throw new NotFoundException("Ce cours n'est pas assigné à cet enseignant");
        var tenantId = tenantContext.getTenantId();
        if (tenantId == null) throw new NotFoundException("Tenant non défini");

        var entity = cahierTexteMapper.toEntity(dto);
        entity.tenantId = tenantId;
        entity.coursId = dto.getCoursId();
        entity.dateCours = dto.getDateCours();
        entity.contenuTraite = dto.getContenuTraite();
        entity.observations = dto.getObservations();
        entity.etapeProgramme = dto.getEtapeProgramme();
        entity.programmeValide = dto.getProgrammeValide() != null ? dto.getProgrammeValide() : false;

        return cahierTexteMapper.toDto(cahierTexteRepository.persist(entity));
    }

    @Override
    public List<CahierTexteDto> listeCahierTexte(UUID enseignantId, UUID coursId) {
        var coursIds = coursRepository.findByProfesseurId(enseignantId).stream()
                .map(c -> c.id).toList();
        if (coursId != null && !coursIds.contains(coursId)) return List.of();
        var ids = coursId != null ? List.of(coursId) : coursIds;
        if (ids.isEmpty()) return List.of();
        return ids.stream()
                .flatMap(id -> cahierTexteRepository.findByCoursId(id).stream())
                .map(cahierTexteMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CahierTexteDto modifierCahierTexte(UUID enseignantId, UUID id, CahierTexteDto dto) {
        var entity = cahierTexteRepository.findById(id);
        if (entity == null) throw new NotFoundException("Cahier de texte non trouvé");
        var cours = coursRepository.findById(entity.coursId);
        if (cours == null || !enseignantId.equals(cours.professeurId))
            throw new NotFoundException("Ce cahier de texte n'est pas modifiable par cet enseignant");

        if (dto.getContenuTraite() != null) entity.contenuTraite = dto.getContenuTraite();
        if (dto.getObservations() != null) entity.observations = dto.getObservations();
        if (dto.getEtapeProgramme() != null) entity.etapeProgramme = dto.getEtapeProgramme();
        if (dto.getProgrammeValide() != null) entity.programmeValide = dto.getProgrammeValide();

        return cahierTexteMapper.toDto(cahierTexteRepository.persist(entity));
    }
}
