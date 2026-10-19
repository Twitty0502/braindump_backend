package org.braindump.websocket;

import io.quarkus.websockets.next.OpenConnections;
import io.quarkus.websockets.next.UserData;
import io.quarkus.websockets.next.WebSocketConnection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class BoardEventService {

    @Inject
    OpenConnections connections;

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
}