package chat;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class ChatServer {
	private ServerSocket serverSocket;
	private List<User> users;
	private List<Room> rooms;
	
	public void startServer() {
		
	}
	
	public void handleClientConnection(Socket clientSocket) {
		
	}
	
	public void broadcastMessage(String message, Room room) {
		
	}
	
	public Room createRoom(String roomName) {

		Room r = new Room(roomName);
		return r;
		
	}
	
	public List<Room> listRooms() {
		
		return rooms;
		
	}

}

