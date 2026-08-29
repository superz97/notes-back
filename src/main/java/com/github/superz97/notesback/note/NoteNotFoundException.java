package com.github.superz97.notesback.note;

public class NoteNotFoundException extends RuntimeException {

    public NoteNotFoundException(Long noteId) {
        super("Note not found: " + noteId);
    }

}
