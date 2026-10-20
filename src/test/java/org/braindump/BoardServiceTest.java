package org.braindump;

import static org.junit.jupiter.api.Assertions.*;

import org.braindump.model.BoardEntity;
import org.braindump.service.BoardService;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@QuarkusTest
class BoardServiceTest {

    @Inject
    BoardService boardService;

    @Test
    @Transactional
    void createBoardWithValidNameSavesBoard() {
        BoardEntity board = new BoardEntity();
        board.setName("Testing");

        BoardEntity result = boardService.createBoard(board);

        assertNotNull(result);
        assertEquals("Testing", result.getName());
        assertNotNull(result.getId());
    }

    @Test
    @Transactional
    void createdBoardsHaveUniqueIds() {
        BoardEntity first = new BoardEntity();
        first.setName("Room One");

        BoardEntity second = new BoardEntity();
        second.setName("Room Two");

        BoardEntity result1 = boardService.createBoard(first);
        BoardEntity result2 = boardService.createBoard(second);

        assertNotNull(result1.getId());
        assertNotNull(result2.getId());
        assertNotEquals(result1.getId(), result2.getId());
    }
}