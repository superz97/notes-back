package com.github.superz97.notesback.note;

import com.github.superz97.notesback.user.User;
import com.github.superz97.notesback.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    NoteRepository noteRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    NoteService noteService;

    @Test
    void createsNoteForUser() {
        User user = User.builder().id(1L).build();
        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(noteRepository.save(any(Note.class))).thenAnswer(inv -> inv.getArgument(0));

        Note note = noteService.createNote(1L, "Title", "Body");

        assertThat(note.getTitle()).isEqualTo("Title");
        assertThat(note.getBody()).isEqualTo("Body");
        assertThat(note.getUser()).isEqualTo(user);
    }

    @Test
    void getsOwnedNote() {
        User owner = User.builder().id(1L).build();
        Note note = Note.builder().id(10L).user(owner).title("T").build();
        when(noteRepository.findById(10L)).thenReturn(Optional.of(note));

        Note result = noteService.getNote(1L, 10L);

        assertThat(result).isEqualTo(note);
    }

    @Test
    void throwsNotFoundWhenNoteMissing() {
        when(noteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> noteService.getNote(1L, 99L)).isInstanceOf(NoteNotFoundException.class);
    }

    @Test
    void throwsNotFoundWhenNoteBelongsToAnotherUser() {
        User owner = User.builder().id(2L).build();
        Note note = Note.builder().id(10L).user(owner).title("T").build();
        when(noteRepository.findById(10L)).thenReturn(Optional.of(note));

        assertThatThrownBy(() -> noteService.getNote(1L, 10L)).isInstanceOf(NoteNotFoundException.class);
    }

    @Test
    void updatesOwnedNote() {
        User owner = User.builder().id(1L).build();
        Note note = Note.builder().id(10L).user(owner).title("Old").body("OldBody").build();
        when(noteRepository.findById(10L)).thenReturn(Optional.of(note));

        Note result = noteService.updateNote(1L, 10L, "New", "NewBody");

        assertThat(result.getTitle()).isEqualTo("New");
        assertThat(result.getBody()).isEqualTo("NewBody");
    }

    @Test
    void deletesOwnedNote() {
        User owner = User.builder().id(1L).build();
        Note note = Note.builder().id(10L).user(owner).title("T").build();
        when(noteRepository.findById(10L)).thenReturn(Optional.of(note));

        noteService.deleteNote(1L, 10L);

        verify(noteRepository).delete(note);
    }

    @Test
    void deletingAnotherUsersNoteThrowsNotFound() {
        User owner = User.builder().id(2L).build();
        Note note = Note.builder().id(10L).user(owner).title("T").build();
        when(noteRepository.findById(10L)).thenReturn(Optional.of(note));

        assertThatThrownBy(() -> noteService.deleteNote(1L, 10L)).isInstanceOf(NoteNotFoundException.class);

        verify(noteRepository, never()).delete(any());
    }

}
