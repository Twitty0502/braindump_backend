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

    // Körs när en användare ansluter till en tavla.
    @OnOpen
    public void onOpen() {
        String boardId = connection.pathParam("boardId");

        // Spara vilken tavla den här anslutningen tillhör.
        connection.userData().put(
                UserData.TypedKey.forString("boardId"),
                boardId);
    }

    // Körs när användaren kopplas från.
    @OnClose
    public void onClose() {
        // Quarkus hanterar själva stängningen av anslutningen.
    }

    // Hanterar meddelanden som klienten skickar till servern.
    @OnTextMessage
    public String onMessage(String message) {
        return "Received: " + message;
    }
}