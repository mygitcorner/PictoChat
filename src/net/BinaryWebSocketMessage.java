package net;

import java.io.IOException;
import java.nio.ByteBuffer;

import jakarta.websocket.Session;

public class BinaryWebSocketMessage extends AbstractWebSocketMessage {

    private ByteBuffer buffer;

    public BinaryWebSocketMessage(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void send(Session session) throws IOException {
        session.getBasicRemote().sendBinary(buffer);
    }
    
}
