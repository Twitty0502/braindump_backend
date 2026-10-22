package org.braindump.websocket;

import io.quarkus.websockets.next.OnClose;
import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.UserData;
import io.quarkus.websockets.next.WebSocket;
import io.quarkus.websockets.next.WebSocketConnection;
import jakarta.inject.Inject;

@WebSocket(path = "/ws/boards/{boardId}")
public class BoardWebSocket {

    @Inject
    WebSocketConnection connection;

    @Inject
    BoardEventService boardEventService;

    // Körs när en användare ansluter till en tavla.
    @OnOpen
    public void onOpen() {
        String boardId = connection.pathParam("boardId");
        System.out.println("WebSocket ansluten till board: " + boardId);
        // Spara vilken tavla den här anslutningen tillhör.
        connection.userData().put(
                UserData.TypedKey.forString("boardId"),
                boardId);

        // Hämta query-strängen från WebSocket-anslutningen.
        String query = connection.handshakeRequest().query();
        System.out.println("WebSocket query: " + query);
        // Läs ut participantId från query-strängen.
        String participantId = null;

        // Kontrollerar att parametrar finns
        if (query != null) {
            // Split/delar upp eftersom flera parametrar kan skickas
            for (String parameter : query.split("&")) {
                // Checkar så att parametern börjar med participantId=
                if (parameter.startsWith("participantId=")) {

                    // Hämtar sedan endast DeltagarID genom att ta bort participantID=
                    participantId = parameter.substring("participantId=".length());
                    break;
                }
            }
        }
        // Spara deltagarens ID om det skickades med i anslutningen.
        if (participantId != null && !participantId.isBlank()) {
            connection.userData().put(
                    UserData.TypedKey.forString("participantId"),
                    participantId);
        }
        boardEventService.broadcastParticipants(boardId);
        System.out.println("Deltagaruppdatering skickad för board: " + boardId);
    }

    // Körs när användaren kopplas från.
    @OnClose
    public void onClose() {
        // Hämta board ID för anslutningen som stängs
        String boardId = connection.userData().get(UserData.TypedKey.forString("boardId"));

        // Uppdaterar deltagarlistan ifall någon lämnar
        if (boardId != null) {
            boardEventService.broadcastParticipants(boardId);
        }
    }

    // Hanterar meddelanden som klienten skickar till servern.
    @OnTextMessage
    public String onMessage(String message) {
        return "Received: " + message;
    }
}