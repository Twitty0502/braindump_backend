package org.braindump.service;

import java.util.List;
import java.util.UUID;

import org.braindump.ai.IdeaCategoriesAiService;
import org.braindump.enums.IdeaCategories;
import org.braindump.model.BoardEntity;
import org.braindump.model.IdeaEntity;
import org.braindump.model.ParticipantEntity;
import org.braindump.repository.BoardRepository;
import org.braindump.repository.IdeaRepository;
import org.braindump.repository.ParticipantRepository;
import org.braindump.websocket.BoardEventService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class IdeaService {

        private final IdeaRepository ideaRepository;
        private final BoardRepository boardRepository;
        private final ParticipantRepository participantRepository;
        private final BoardEventService boardEventService;
        private final IdeaCategoriesAiService ideaCategoriesAiService;

        public IdeaService(
                        IdeaRepository ideaRepository,
                        BoardRepository boardRepository,
                        ParticipantRepository participantRepository,
                        BoardEventService boardEventService,
                        IdeaCategoriesAiService ideaCategoriesAiService) {
                this.ideaRepository = ideaRepository;
                this.boardRepository = boardRepository;
                this.participantRepository = participantRepository;
                this.boardEventService = boardEventService;
                this.ideaCategoriesAiService = ideaCategoriesAiService;
        }

        public List<IdeaEntity> listAll() {
                return ideaRepository.listAll();
        }

        // Hämta alla idéer kopplade till en specifik board
        public List<IdeaEntity> listByBoard(UUID boardId) {

                // Kontrollera att boarden finns i backend innan hämtning
                boardRepository.findByIdOptional(boardId)
                                .orElseThrow(() -> new NotFoundException("Board not found with id: " + boardId));
                return ideaRepository.list("board.id", boardId);
        }

        @Transactional
        public IdeaEntity createIdea(String title, String description, UUID boardId, Long participantId) {

                if (title == null || title.isBlank()) {
                        throw new BadRequestException("Title cannot be empty");
                }

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
                                .orElseThrow(() -> new NotFoundException(
                                                "Participant not found with id: " + participantId));
                idea.setParticipant(participant);

                // Se till att deltagaren är kopplad till rätt board innan idén kan skapas
                if (participant.getBoard() == null || !participant.getBoard().getId().equals(boardId)) {
                        throw new BadRequestException(
                                        "Participant with id: " + participantId + " is not part of board with id: "
                                                        + boardId);
                }

                // Kategorisera idéer genom AI
                // Idéns titel tillsammans med beskrivning, ifall beskrivningen är tom använd
                // tom sträng
                String ideaText = title + "\n" + (description == null ? "" : description);
                // Kategorisera idén utifrån textens innehåll
                IdeaCategories category = ideaCategoriesAiService.categorizeIdea(ideaText);
                idea.setCategories(category);

                // Spara idén i databasen
                ideaRepository.persist(idea);

                // vertx är klass för att skapa och hantera JSON data vilket vi i detta fall
                // använder för att paketera händelen när en idé skapas
                // vi tar nu in information kring idens id, titel, beskrivning, deltagarens id
                // samt namn.
                io.vertx.core.json.JsonObject ideaData = new io.vertx.core.json.JsonObject()
                                .put("id", idea.getId())
                                .put("title", idea.getTitle())
                                .put("description", idea.getDescription())
                                .put("categories", idea.getCategories().name())
                                .put("participant",
                                                new io.vertx.core.json.JsonObject()
                                                                .put("id", participant.getId())
                                                                .put("displayName",
                                                                                participant.getDisplayName()));
                // Beskriv vilken händelse som har inträffat.
                io.vertx.core.json.JsonObject event = new io.vertx.core.json.JsonObject()
                                .put("type", "IDEA_CREATED")
                                .put("boardId", boardId.toString())
                                .put("idea", ideaData);

                // Skicka händelsen till deltagarna på den aktuella boarden.
                boardEventService.broadcastToBoard(
                                boardId.toString(),
                                event.encode());

                return idea;
        }

        // Radera en idé baserat på dess id
        @Transactional
        public void deleteIdea(Long id) {
                IdeaEntity idea = ideaRepository.findByIdOptional(id)
                                .orElseThrow(() -> new NotFoundException(
                                                "Idea not found with id: " + id));

                // Hämta boardId från idén innan den tas bort
                UUID boardId = idea.getBoard().getId();

                // Radera iden från databasen
                ideaRepository.delete(idea);

                // Uppdatera ändringar i realtid för anslutna
                boardEventService.broadcastToBoard(boardId.toString(),
                                "{\"type\":\"IDEA_DELETED\",\"ideaId\":" + id
                                                + ",\"boardId\":\"" + boardId + "\"}");
        }

        // Rensa alla idéer kopplade till en specifik board
        @Transactional
        public void clearBoard(UUID boardId) {
                boardRepository.findByIdOptional(boardId)
                                .orElseThrow(() -> new NotFoundException(
                                                "Board not found with id: " + boardId));

                // Radera alla idéer från specifik boardId
                ideaRepository.delete("board.id", boardId);

                boardEventService.broadcastToBoard(
                                boardId.toString(),
                                "{\"type\":\"BOARD_CLEARED\",\"boardId\":\"" + boardId + "\"}");
        }
}