package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
	private static ServerSocket serverSocket;
	private static List<User> users = new ArrayList<User>();
	private static List<Room> rooms = new ArrayList<Room>();
	private static int port = 8080;

	public static void main(String[] args) {
		startServer();
	}

	public static void startServer() {
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Servidor iniciado correctamente");

			while (true) {
				Socket clientSocket = serverSocket.accept();
				System.out.println("Un usuario se ha conectado desde: " + clientSocket.getInetAddress().getHostAddress());
				Thread thread = new Thread(new ClientHandler(clientSocket));
				thread.start();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(2);
		}
	}

	public static class ClientHandler implements Runnable {
		private Socket clientSocket;
		private PrintWriter out;
		private BufferedReader in;
		private User user;

		public ClientHandler(Socket socket) {
			this.clientSocket = socket;
		}

		@Override
		public void run() {
			try {
				out = new PrintWriter(clientSocket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

				out.println("Conexión establecida con éxito.");

				String username = null;
				while (username == null || username.isEmpty()) {
					username = in.readLine();
					user = new User(username, clientSocket);
					users.add(user);
				}

				out.println("Bienvenido/a, " + username + ".");
				out.println("Lista de comandos:");
				out.println("/list - Lista todas las salas disponibles");
				out.println("/create [nombre_sala] - Crea una nueva sala");
				out.println("/join [nombre_sala] - Únete a una sala existente");
				out.println("/delete [nombre_sala]- Elimina sala");
				out.println("/leave - Salir de la sala");
				out.println("Si necesitas volver a ver los comandos disponibles, escribe /help");

				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					if (inputLine.startsWith("/")) {
						handleCommand(inputLine);
					} else {
						broadcastMessage(user.getUsername() + ": " + inputLine, user.getCurrentRoom());
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(2);
			} finally {
				try {
					if (user != null) {
						users.remove(user);
						user.leaveRoom();
						if (user.getCurrentRoom() == null) {
							out.println("Primero debe unirse a una sala.");
						}
					}
					clientSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(2);
				}
			}
		}

		public void handleCommand(String command) {
			String[] tokens = command.split("\\s+");

			switch (tokens[0]) {
			case "/help":
				out.println("Lista de comandos:");
				out.println("/list - Lista todas las salas disponibles");
				out.println("/create [nombre_sala] - Crea una nueva sala");
				out.println("/join [nombre_sala] - Únete a una sala existente");
				out.println("/delete [nombre_sala]- Elimina sala");
				out.println("/leave - Salir de la sala");
				
				break;
			case "/list":
				if (rooms.isEmpty()) {
					out.println("No existen salas. ¿Qué tal si creas una?");
				} else {
					out.println("Salas disponibles:");
					for (Room room : rooms) {
						out.println("- " + room.getName()+" ("+room.listUsers().size()+" usuarios)");
					}
					out.println("/join para unirse o /delete para eleminar");
				}
				break;
			case "/create":
				if (tokens.length < 2) {
					out.println("Uso: /create [nombre_sala]");
				} else {
					String roomName = tokens[1];
					Room room = createRoom(roomName);
					if (room != null) {
						user.joinRoom(room);
						out.println("Sala '" + roomName + "' creada. Te has unido a ella correctamente.");
						out.println("Escriba el mensaje:");

					} else {
						out.println("La sala '" + roomName + "' ya existe.");
					}
				}
				break;
			case "/join":
				if (tokens.length < 2) {
					out.println("Uso: /join [nombre_sala]");
					out.println("Escriba el mensaje:");
				} else {
					String roomName = tokens[1];
					Room room = findRoom(roomName);
					if (room != null) {
						user.joinRoom(room);
						out.println("Te has unido con exito a la sala '" + roomName + "'.");
					} else {
						out.println("La sala '" + roomName + "' no existe.");
					}
				}
				break;

			case "/delete":
				if (tokens.length < 2) {
					out.println("Uso: /delete [nombre_sala]");
				} else {
					String roomNameToRemove = tokens[1];
					Room roomToRemove = null;
					for (Room room : rooms) {
						if (room.getName().equals(roomNameToRemove)) {
							roomToRemove = room;
							break;
						}
					}
					if (roomToRemove != null) {
						rooms.remove(roomToRemove);
						out.println("Sala '" + roomNameToRemove + "' eliminada correctamente.");
					} else {
						out.println("La sala '" + roomNameToRemove + "' no existe.");
					}
				}
				break;

			case "/leave":
				if (user.getCurrentRoom() != null) {
					user.leaveRoom();
					out.println("Usuario desconectado de la sala");
				} else {
					out.println("Desconectado del chat.");
				}
				break;

			default:
				out.println("Comando no reconocido. Escribe /help para ver la lista de comandos disponibles.");

			}
		}
	}

	public synchronized static void broadcastMessage(String message, Room room) {
		if (room != null) {
			List<User> roomUsers = room.listUsers();
			for (User user : roomUsers) {
				user.sendMessage(message);
			}
		} else {
			System.out.println("El usuario no se encuentra en ninguna sala");
		}
	}

	public static Room findRoom(String roomName) {
		for (Room room : rooms) {
			if (room.getName().equalsIgnoreCase(roomName)) {
				return room;
			}
		}
		return null;
	}

	public synchronized static Room createRoom(String roomName) {
		for (Room room : rooms) {
			if (room.getName().equals(roomName)) {
				return null; // Si la sala existe, no crea nada
			}
		}

		// Si no existe, la crea
		Room newRoom = new Room(roomName);
		rooms.add(newRoom);
		return newRoom;
	}

}