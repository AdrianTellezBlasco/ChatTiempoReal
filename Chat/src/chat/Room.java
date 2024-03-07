package chat;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private String name;
    private List<User> users;
    
    public Room(String name) {
        this.name = name;
        this.users = new ArrayList<>();
    }

    public synchronized void addUser(User user) {
        users.add(user);
    }

    public synchronized void removeUser(User user) {
        users.remove(user);
    }

    public synchronized void sendMessage(String message, User sender) {
        for (User user : users) {
            if (!user.equals(sender)) {
                user.sendMessage(message);
            }
        }
    }

    public synchronized List<User> listUsers() {
        return users;
    }

    public String getName() {
        return name;
    }
}