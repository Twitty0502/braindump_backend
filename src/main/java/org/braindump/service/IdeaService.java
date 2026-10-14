package org.braindump.service;

import org.braindump.model.BoardEntity;
import org.braindump.model.IdeaEntity;
import org.braindump.repository.BoardRepository;
import org.braindump.repository.IdeaRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class IdeaService {

    private final IdeaRepository ideaRepository;
    private final BoardRepository boardRepository;

    public IdeaService(
            IdeaRepository ideaRepository,
            BoardRepository boardRepository) {
        this.ideaRepository = ideaRepository;
        this.boardRepository = boardRepository;
    }

    @Transactional
    public IdeaEntity createIdea(String title, String description, Long boardId) {

        BoardEntity board = boardRepository.findByIdOptional(boardId)
                .orElseThrow(() -> new NotFoundException("Board not found with id: " + boardId));
        IdeaEntity idea = new IdeaEntity();

        // Skapa idén
        idea.setTitle(title);
        idea.setDescription(description);

        // Koppla idén till tavlan/boarden
        idea.setBoard(board);

        // Spara idén i databasen
        ideaRepository.persist(idea);
        return idea;
    }
}
