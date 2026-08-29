package com.github.superz97.notesback.note;

import com.github.superz97.notesback.common.GlobalExceptionHandler;
import com.github.superz97.notesback.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@WebMvcTest(NoteController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class NoteControllerTest {

    @Autowired MockMvcTester mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean NoteService noteService;
    @MockitoBean NoteMapper noteMapper;

    @Test
    void createReturns201() {
        User user = User.builder().id(1L).build();
        Note note = Note.builder().id(1L).user(user).title("Title").body("Body").build();
        when(noteService.createNote(anyLong(), any(), any())).thenReturn(note);
        when(noteMapper.toResponse(note)).thenReturn(new NoteResponse(1L, "Title", "Body", null, null));

        var result = mockMvc.post().uri("/api/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new NoteRequest("Title", "Body")))
                .exchange();

        assertThat(result)
                .hasStatus(201)
                .bodyJson()
                .extractingPath("$.title").isEqualTo("Title");
    }

    @Test
    void createWithBlankTitleReturns400() {
        var result = mockMvc.post().uri("/api/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new NoteRequest("", "Body")))
                .exchange();

        assertThat(result)
                .hasStatus(400)
                .bodyJson()
                .extractingPath("$.title").isEqualTo("Validation Error");
    }

    @Test
    void getReturns404WhenNoteNotFound() {
        when(noteService.getNote(anyLong(), any())).thenThrow(new NoteNotFoundException(99L));

        var result = mockMvc.get().uri("/api/notes/99").exchange();

        assertThat(result)
                .hasStatus(404)
                .bodyJson()
                .extractingPath("$.title").isEqualTo("Resource Not Found");
    }

    @Test
    void deleteReturns204() {
        var result = mockMvc.delete().uri("/api/notes/1").exchange();

        assertThat(result).hasStatus(204);
    }

    @Test
    void listReturnsPageOfNotes() {
        when(noteService.listNotes(anyLong(), any())).thenReturn(new PageImpl<>(List.of()));

        var result = mockMvc.get().uri("/api/notes").exchange();

        assertThat(result)
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.content").asArray().isEmpty();
    }

}
