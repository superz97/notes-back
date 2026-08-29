package com.github.superz97.notesback.note;

import java.time.Instant;

public record NoteResponse(
        Long id,
        String title,
        String body,
        Instant createdAt,
        Instant updatedAt
) {
}
