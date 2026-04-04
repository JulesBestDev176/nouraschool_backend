package com.nouraschool.domain.usecases.admin;

import com.nouraschool.domain.dtos.NoteDto;

import java.util.List;
import java.util.UUID;

public interface AdminNoteUseCase {

    List<NoteDto> findAll();

    NoteDto findById(UUID id);

    NoteDto create(NoteDto dto);

    NoteDto update(UUID id, NoteDto dto);

    void delete(UUID id);
}
