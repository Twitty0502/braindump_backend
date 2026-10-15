package org.braindump.service;

import org.braindump.model.BoardEntity;
import org.braindump.model.IdeaEntity;
import org.braindump.model.ParticipantEntity;
import org.braindump.repository.BoardRepository;
import org.braindump.repository.IdeaRepository;
import org.braindump.repository.ParticipantRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class IdeaService {

    private final IdeaRepository ideaRepository;
    private final BoardRepository boardRepository;
    private final ParticipantRepository participantRepository;

    public IdeaService(
            IdeaRepository ideaRepository,
            BoardRepository boardRepository,
            ParticipantRepository participantRepository) {
        this.ideaRepository = ideaRepository;
        this.boardRepository = boardRepository;
        this.participantRepository = participantRepository;
    }

    @Transactional
    public IdeaEntity createIdea(String title, String description, Long boardId, Long participantId) {

        BoardEntity board = boardRepository.findByIdOptional(boardId)
                .orElseThrow(() -> new NotFoundException("Board not found with id: " + boardId));
        IdeaEntity idea = new IdeaEntity();

        // Skapa idén
        idea.setTitle(title);
        idea.setDescription(description);

        // Koppla idén till tavlan/boarden
        idea.setBoard(board);

        // Koppla idén till deltagaren
        // Se till att deltagaren finns innan idén skapas
        ParticipantEntity participant = participantRepository.findByIdOptional(participantId)
                .orElseThrow(() -> new NotFoundException("Participant not found with id: " + participantId));
        idea.setParticipant(participant);

        // Se till att deltagaren är kopplad till rätt board innan idén kan skapas
        if (participant.getBoard() == null || !participant.getBoard().getId().equals(boardId)) {
            throw new BadRequestException(
                    "Participant with id: " + participantId + " is not part of board with id: " + boardId);
        }
        // Spara idén i databasen
        ideaRepository.persist(idea);
        return idea;
    }
}
