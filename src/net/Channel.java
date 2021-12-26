package net;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Channel {
    public enum Role {
        OPERATOR('o');

        private char symbol;

        Role(char symbol) {
            this.symbol = symbol;
        }

        public char symbol() {
            return symbol;
        }
    }

    public class UserRoles {
        private Set<Role> roles;

        public UserRoles() {
            roles = new HashSet<>();
        }

        public boolean setRole(Role role) {
            return roles.add(role);
        }

        public boolean unsetRole(Role role) {
            return roles.remove(role);
        }

        public boolean hasRole(Role role) {
            return roles.contains(role);
        }

        public Collection<Role> list() {
            return Collections.unmodifiableCollection(roles);
        }
    }

    private static Map<String, Channel> channels = new HashMap<>();

    private String name;
    private Set<User> users;

    private Map<User, UserRoles> userRoleMap;
    private String operatorPassword;

    public static Channel create(String name) {
        Channel channel = channels.get(name);
        if (channel == null) {
            channel = new Channel(name);
            channels.put(name, channel);
        }
        return channel;
    }

    public static boolean exists(String name) {
        return channels.get(name) != null;
    }

    public static Channel get(String name) {
        return channels.get(name);
    }

    public static Collection<Channel> list() {
        return Collections.unmodifiableCollection(channels.values());
    }

    private Channel(String name) {
        this.name = name;
        users = new HashSet<>();
        userRoleMap = new HashMap<>();

    }

    public String name() {
        return name;
    }

    public Collection<User> users() {
        return Collections.unmodifiableCollection(users);
    }

    public synchronized boolean addUser(User user) {
        if (!users.add(user)) {
            return false;
        }
        userRoleMap.put(user, new UserRoles());
        user.addChannel(this);
        return true;
    }

    public synchronized User getUser(String nickName) {
        return users.stream()
                    .filter(u -> u.nickName().equals(nickName))
                    .findFirst()
                    .orElse(null);
    }

    public synchronized boolean removeUser(User user) {
        if (!users.remove(user)) {
            return false;
        }

        user.removeChannel(this);
        userRoleMap.remove(user);
        
        return true;
    }

    public UserRoles roles(User user) {
        return userRoleMap.get(user);
    }

    public String operatorPassword() {
        return operatorPassword;
    }

    public void setOperatorPassword(String password) {
        operatorPassword = password;
    }

    @Override
    public boolean equals(Object other) {
        if (getClass() != other.getClass()) {
            return false;
        }

        Channel otherChannel = (Channel)other;

        return name.equals(otherChannel.name);
    }
}
