package net;

import java.io.IOException;

import jakarta.websocket.Session;

public abstract class AbstractWebSocketMessage {
    
    public abstract void send(Session session) throws IOException;
}
