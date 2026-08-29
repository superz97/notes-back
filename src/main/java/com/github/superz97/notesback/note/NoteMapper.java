package com.github.superz97.notesback.note;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NoteMapper {
    NoteResponse toResponse(Note note);
}
