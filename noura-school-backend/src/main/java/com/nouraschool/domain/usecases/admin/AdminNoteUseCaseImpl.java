package com.nouraschool.domain.usecases.admin;

import com.nouraschool.domain.dtos.NoteDto;
import com.nouraschool.domain.entities.NoteEntity;
import com.nouraschool.domain.exception.errors.NotFoundException;
import com.nouraschool.domain.mappers.NoteMapper;
import com.nouraschool.domain.repositories.EleveRepository;
import com.nouraschool.domain.repositories.MatiereRepository;
import com.nouraschool.domain.repositories.NoteRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class AdminNoteUseCaseImpl implements AdminNoteUseCase {

    @Inject
    NoteRepository noteRepository;
    @Inject
    EleveRepository eleveRepository;
    @Inject
    MatiereRepository matiereRepository;
    @Inject
    NoteMapper noteMapper;

    @Override
    public List<NoteDto> findAll() {
        return noteRepository.findAll().stream().map(noteMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public NoteDto findById(UUID id) {
        NoteEntity entity = noteRepository.findById(id);
        if (entity == null) throw new NotFoundException("Note not found: " + id);
        return noteMapper.toDto(entity);
    }

    @Override
    @Transactional
    public NoteDto create(NoteDto dto) {
        NoteEntity entity = noteMapper.toEntity(dto);
        if (dto.getEleveId() != null) entity.eleve = eleveRepository.findById(dto.getEleveId());
        if (dto.getMatiereId() != null) entity.matiere = matiereRepository.findById(dto.getMatiereId());
        return noteMapper.toDto(noteRepository.persist(entity));
    }

    @Override
    @Transactional
    public NoteDto update(UUID id, NoteDto dto) {
        NoteEntity entity = noteRepository.findById(id);
        if (entity == null) throw new NotFoundException("Note not found: " + id);
        if (dto.getEleveId() != null) entity.eleve = eleveRepository.findById(dto.getEleveId());
        if (dto.getMatiereId() != null) entity.matiere = matiereRepository.findById(dto.getMatiereId());
        if (dto.getTypeEvaluation() != null) entity.typeEvaluation = dto.getTypeEvaluation();
        if (dto.getNote() != null) entity.note = dto.getNote();
        if (dto.getNoteSur() != null) entity.noteSur = dto.getNoteSur();
        if (dto.getTrimestre() != null) entity.trimestre = dto.getTrimestre();
        if (dto.getAnneeScolaire() != null) entity.anneeScolaire = dto.getAnneeScolaire();
        if (dto.getDateEvaluation() != null) entity.dateEvaluation = dto.getDateEvaluation();
        if (dto.getCommentaire() != null) entity.commentaire = dto.getCommentaire();
        return noteMapper.toDto(noteRepository.persist(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        NoteEntity entity = noteRepository.findById(id);
        if (entity == null) throw new NotFoundException("Note not found: " + id);
        noteRepository.delete(entity);
    }
}
