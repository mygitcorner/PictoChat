package net;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import jakarta.websocket.Session;

public class User {
    private static Map<String, User> users = new HashMap<>();

    private String nickName;
    private List<Channel> channels;
    private Session session;

    public static User get(String nickName) {
        return users.get(nickName);
    }

    public static boolean exists(String nickName) {
        return get(nickName) != null;
    }

    public static Collection<User> list() {
        return Collections.unmodifiableCollection(users.values());
    }

    public User(Session session) {
        nickName = null;
        channels = new LinkedList<>();
        this.session = session;
    }

    public boolean hasNickName() {
        return nickName != null;
    }

    public boolean setNickName(String nickName) {
        synchronized (users) {
            if (!exists(nickName)) {
                this.nickName = nickName;
                users.put(nickName, this);
                return true;
            }
        }

        return false;
    }

    public String nickName() {
        return nickName;
    }

    public Session session() {
        return session;
    }

    public Collection<Channel> channels() {
        return Collections.unmodifiableCollection(channels);
    }

    public void addChannel(Channel channel) {
        channels.add(channel);
    }

    public void removeChannel(Channel channel) {
        channels.remove(channel);
    }

    public boolean hasChannel(Channel channel) {
        return channels.contains(channel);
    }

    public synchronized void remove() {
        if (hasNickName()) {
            for (Channel channel : channels) {
                channel.removeUser(this);
                ReplyHelper.sendChannel(channel, new StringWebSocketMessage("PART " + channel.name() + " " + nickName));
            }

            users.remove(nickName);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (getClass() != other.getClass()) {
            return false;
        }

        User otherUser = (User) other;

        return nickName.equals(otherUser.nickName);
    }
}
