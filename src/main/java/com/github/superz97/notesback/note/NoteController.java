package com.github.superz97.notesback.note;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;
    private final NoteMapper noteMapper;

    // TODO: replace with userId extracted from JWT
    private static final Long PLACEHOLDER_USER_ID = 1L;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NoteResponse create(@Valid @RequestBody NoteRequest request) {
        Note note = noteService.createNote(PLACEHOLDER_USER_ID, request.title(), request.body());
        return noteMapper.toResponse(note);
    }

    @GetMapping
    public Page<NoteResponse> list(Pageable pageable) {
        return noteService.listNotes(PLACEHOLDER_USER_ID, pageable).map(noteMapper::toResponse);
    }

    @GetMapping("/{id}")
    public NoteResponse get(@PathVariable Long id) {
        return noteMapper.toResponse(noteService.getNote(PLACEHOLDER_USER_ID, id));
    }

    @PutMapping("/{id}")
    public NoteResponse update(@PathVariable Long id, @Valid @RequestBody NoteRequest request) {
        Note note = noteService.updateNote(PLACEHOLDER_USER_ID, id, request.title(), request.body());
        return noteMapper.toResponse(note);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        noteService.deleteNote(PLACEHOLDER_USER_ID, id);
    }

}
