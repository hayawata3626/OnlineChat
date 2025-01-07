package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import constants.Constants;

/**
 * A simple chat server that manages client connections and broadcasts messages.
 * 
 * @author Wataru Hayatsu
 */
public class ChatServer {

	private ServerSocket serverSocket; // Server socket to accept connections
	private ConcurrentHashMap<Integer, PrintWriter> clients = new ConcurrentHashMap<>(); // Map of connected clients
	private int userId = 1; // Counter to assign unique IDs to clients

	/**
	 * Starts the chat server on the specified port.
	 * 
	 * @param port the port number to listen for client connections
	 */
	public void startServer(int port) {
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Server started on port " + port);

			while (true) {
				Socket clientSocket = serverSocket.accept();
				System.out.println("Client connected from: " + clientSocket.getInetAddress());

				int currentUserId = userId++;
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
				clients.put(currentUserId, out);

				new Thread(() -> handleClient(clientSocket, currentUserId)).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handles communication with a specific client.
	 * 
	 * @param clientSocket the socket for the connected client
	 * @param userId       the unique ID assigned to the client
	 */
	private void handleClient(Socket clientSocket, int userId) {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
			clients.get(userId).println("Welcome! Your User ID is: " + userId);
			String message;
			while ((message = in.readLine()) != null) {
				System.out.println("User " + userId + ": " + message);
				broadcastMessage("User " + userId + ": " + message, userId);
			}
		} catch (Exception e) {
			System.out.println("User " + userId + " is disconnected.");
		} finally {
			clients.remove(userId);
		}
	}

	/**
	 * Broadcasts a message to all connected clients.
	 * 
	 * @param message  the message to broadcast
	 * @param senderId the ID of the client who sent the message
	 */
	private void broadcastMessage(String message, int senderId) {
		for (int id : clients.keySet()) {
			clients.get(id).println(message);
		}
	}

	/**
	 * The main method to run the chat server.
	 * 
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		ChatServer server = new ChatServer();
		server.startServer(Constants.SERVER_PORT);
	}
}
