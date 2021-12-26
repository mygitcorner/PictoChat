package net;

import java.io.IOException;

import jakarta.websocket.Session;

public class StringWebSocketMessage extends AbstractWebSocketMessage {

    private String message;

    public StringWebSocketMessage(String message) {
        this.message = message;
    }

    @Override
    public void send(Session session) throws IOException {
        session.getBasicRemote().sendText(message);
    }
    
}
