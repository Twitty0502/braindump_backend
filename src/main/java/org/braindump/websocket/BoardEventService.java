package org.braindump.websocket;

import org.braindump.model.ParticipantEntity;
import org.braindump.repository.ParticipantRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.websockets.next.OpenConnections;
import io.quarkus.websockets.next.UserData;
import io.quarkus.websockets.next.WebSocketConnection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class BoardEventService {

    @Inject
    OpenConnections connections;

    @Inject
    ParticipantRepository participantRepository;

    @Inject
    ObjectMapper objectMapper;

    // Metod för att skicka meddelanden till alla anslutna användare som är kopplade
    // till samma boardId
    public void broadcastToBoard(String boardId, String message) {
        for (WebSocketConnection connection : connections.listAll()) {
            String connectedBoardId = connection.userData().get(
                    UserData.TypedKey.forString("boardId"));

            // Kontrollera om den anslutna användaren är kopplad till samma boardId
            if (boardId.equals(connectedBoardId)) {
                connection.sendTextAndAwait(message);
            }
        }
    }

    // Skickar namnen på deltagarna som är anslutna till boarden.
    @Transactional
    public void broadcastParticipants(String boardId) {
        System.out.println("Uppdaterar deltagare för board: " + boardId);
        var names = connections.listAll().stream()

                .filter(c -> boardId.equals(c.userData().get(
                        UserData.TypedKey.forString("boardId"))))
                .map(c -> c.userData().get(
                        UserData.TypedKey.forString("participantId")))
                .filter(id -> id != null)
                .distinct()
                // Omvandlar deltagarens ID till Long
                .map(Long::parseLong)

                // Hämtar deltagaren från databasen
                .map(participantRepository::findById)

                // Hoppar över deltagare som inte hittas
                .filter(p -> p != null)

                // Hämtar endast deltagarens namn
                .map(ParticipantEntity::getDisplayName);

        try {
            // Skapar JSON meddelande med typ och deltagarNamn
            String message = objectMapper.writeValueAsString(
                    java.util.Map.of(
                            "type", "PARTICIPANTS_UPDATED",
                            "participants", names));

            // Skickar till alla anslutna
            broadcastToBoard(boardId, message);

        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Kunde inte skapa meddelande med deltagarnamn", e);
        }
    }
}