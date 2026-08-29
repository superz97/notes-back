package com.github.superz97.notesback.note;

import com.github.superz97.notesback.user.User;
import com.github.superz97.notesback.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    @Transactional
    public Note createNote(Long userId, String title, String body) {
        User user = userRepository.getReferenceById(userId);
        Note note = Note.builder()
                .user(user)
                .title(title)
                .body(body)
                .build();
        return noteRepository.save(note);
    }

    @Transactional
    public Note updateNote(Long userId, Long noteId, String title, String body) {
        Note note = getNote(userId, noteId);
        note.setTitle(title);
        note.setBody(body);
        return note;
    }

    public Page<Note> listNotes(Long userId, Pageable pageable) {
        return noteRepository.findAllByUserId(userId, pageable);
    }

    public Note getNote(Long userId, Long noteId) {
        Note note = noteRepository.findById(noteId).orElseThrow(() -> new NoteNotFoundException(noteId));
        if (!note.getUser().getId().equals(userId)) throw new NoteNotFoundException(noteId);
        return note;
    }

    @Transactional
    public void deleteNote(Long userId, Long noteId) {
        Note note = getNote(userId, noteId);
        noteRepository.delete(note);
    }

}
