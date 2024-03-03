package chat;

import java.util.List;

public class Room {
	private String name;
	private List<User> users;
	
	public Room(String name) {
		this.name = name;
	}

	public void addUser(User user) {
			
	}
	
	public void removeUser(User user) {
		
	}
	
	public void sendMessage(String message, User sender) {
		
	}
	
	public List<User> listUsers() {
		return users;
		
	}
}
