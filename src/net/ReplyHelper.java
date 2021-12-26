package net;

import java.io.IOException;
import java.util.Collection;

import jakarta.websocket.Session;

public class ReplyHelper {
    
    public static void sendUser(User user, AbstractWebSocketMessage reply) {
        Session session = user.session();
        if (session.isOpen()) {
            try {
                reply.send(session);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void sendUsers(Collection<? extends User> users, AbstractWebSocketMessage reply) {
        users.forEach(user -> sendUser(user, reply));
    }

    public static void sendChannel(Channel channel, AbstractWebSocketMessage reply) {
        sendUsers(channel.users(), reply);
    }
}
