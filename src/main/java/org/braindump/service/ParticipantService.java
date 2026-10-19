package org.braindump.service;

import java.util.UUID;

import org.braindump.model.BoardEntity;
import org.braindump.model.ParticipantEntity;
import org.braindump.repository.BoardRepository;
import org.braindump.repository.ParticipantRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final BoardRepository boardRepository;

    public ParticipantService(
            ParticipantRepository participantRepository,
            BoardRepository boardRepository) {
        this.participantRepository = participantRepository;
        this.boardRepository = boardRepository;
    }

    @Transactional
    public ParticipantEntity joinBoard(String displayName, UUID boardId) {
        // Kontrollera att displayName inte är null eller tomt
        if (displayName == null || displayName.trim().isEmpty()) {
            throw new BadRequestException("Display name cannot be null or empty");
        }
        // Kontrollera att displayName inte är längre än 15 tecken
        displayName = displayName.trim();
        if (displayName.length() > 15) {
            throw new BadRequestException("Display name cannot exceed 15 characters");
        }
        BoardEntity board = boardRepository.findByIdOptional(boardId)
                .orElseThrow(() -> new NotFoundException("Board not found with id: " + boardId));
        ParticipantEntity participant = new ParticipantEntity();

        // Sätt deltagarens displayName
        participant.setDisplayName(displayName);

        // Koppla deltagaren till tavlan/boarden
        participant.setBoard(board);

        // Spara deltagaren i databasen
        participantRepository.persist(participant);
        return participant;
    }
}
