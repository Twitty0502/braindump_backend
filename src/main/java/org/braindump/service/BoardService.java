package org.braindump.service;

import java.util.List;
import java.util.UUID;

import org.braindump.model.BoardEntity;
import org.braindump.repository.BoardRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class BoardService {

    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public List<BoardEntity> getAllBoards() {
        return boardRepository.listAll();
    }

    public BoardEntity getBoardById(UUID id) {
        return boardRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Board not found with id: " + id));
    }

    @Transactional
    public BoardEntity createBoard(BoardEntity board) {
        board.setId(null); // Försäkrar oss om att id:t är null vid skapandet
        boardRepository.persist(board);
        return board;
    }

    @Transactional
    public BoardEntity updateBoard(UUID id, BoardEntity updatedBoard) {
        BoardEntity activeBoard = getBoardById(id);
        activeBoard.setName(updatedBoard.getName());
        return activeBoard;
    }

    @Transactional
    public void deleteBoard(UUID id) {
        BoardEntity activeBoard = getBoardById(id);
        boardRepository.delete(activeBoard);
    }
}
