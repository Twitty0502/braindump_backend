
package org.braindump;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.braindump.model.BoardEntity;
import org.braindump.model.IdeaEntity;
import org.braindump.model.ParticipantEntity;
import org.braindump.service.BoardService;
import org.braindump.service.IdeaService;
import org.braindump.service.ParticipantService;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;

@QuarkusTest
class IdeaServiceTest {

    @Inject
    BoardService boardService;

    @Inject
    IdeaService ideaService;

    @Inject
    ParticipantService participantService;

    @Test
    @Transactional
    void createIdeaSavesValidIdeaInCorrectBoard() {
        BoardEntity board = new BoardEntity();
        board.setName("Test board");
        board = boardService.createBoard(board);

        ParticipantEntity participant = participantService.joinBoard("Daniel", board.getId());

        IdeaEntity result = ideaService.createIdea(
                "Fixa problem",
                "Fixa ett problem där följande...",
                board.getId(),
                participant.getId());

        assertNotNull(result);
        assertEquals("Fixa problem", result.getTitle());
        assertEquals("Fixa ett problem där följande...", result.getDescription());
        assertEquals(board.getId(), result.getBoard().getId());
        assertEquals(participant.getId(), result.getParticipant().getId());
    }

    @Test
    void createIdeaRejectsNullTitle() {
        assertThrows(BadRequestException.class,
                () -> ideaService.createIdea(
                        null, "Description", UUID.randomUUID(), 1L));
    }

    @Test
    void createIdeaRejectsEmptyTitle() {
        assertThrows(BadRequestException.class,
                () -> ideaService.createIdea(
                        "", "Description", UUID.randomUUID(), 1L));
    }

    @Test
    void createIdeaRejectsTitleContainingOnlyWhitespace() {
        assertThrows(BadRequestException.class,
                () -> ideaService.createIdea(
                        "   ", "Description", UUID.randomUUID(), 1L));
    }
}
