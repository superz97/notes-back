package com.github.superz97.notesback.note;

import jakarta.validation.constraints.NotBlank;

public record NoteRequest(@NotBlank String title, String body) {
}
