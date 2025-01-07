package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import constants.Constants;

/**
 * A simple chat client that connects to a chat server using sockets.
 * Provides a GUI for the user to send and receive messages.
 * 
 * @author Wataru Hayatsu
 */
public class ChatClient {

    private Socket socket; // Socket for connecting to the server
    private PrintWriter out; // Stream to send messages to the server
    private BufferedReader in; // Stream to receive messages from the server

    private JFrame frame; // Main application window
    private JTextArea messageArea; // Area to display received messages
    private JTextField inputField; // Input field for typing messages
    private JButton sendButton; // Button to send messages

    /**
     * Constructs a ChatClient and connects it to the server.
     * 
     * @param serverAddress the address of the server
     * @param port          the port number of the server
     */
    public ChatClient(String serverAddress, int port) {
        try {
            socket = new Socket(serverAddress, port);
            System.out.println("Connected to server: " + serverAddress + ":" + port);

            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            initializeUI();
            new Thread(this::receiveMessages).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the graphical user interface.
     */
    private void initializeUI() {
        frame = new JFrame("Chat Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        messageArea = new JTextArea();
        messageArea.setEditable(false);
        frame.add(new JScrollPane(messageArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("Send");
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        frame.add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(this::sendMessage);
        inputField.addActionListener(this::sendMessage);

        frame.setVisible(true);
    }

    /**
     * Sends a message to the server.
     * Displays an error message if the input is empty.
     * 
     * @param e the action event triggered by clicking the send button
     */
    private void sendMessage(ActionEvent e) {
        String message = inputField.getText().trim();
        if (message.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please input a valid message.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            out.println(message);
            inputField.setText("");
        }
    }

    /**
     * Receives messages from the server and displays them in the message area.
     */
    private void receiveMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                messageArea.append(message + "\n");
            }
        } catch (Exception e) {
            System.out.println("Connection closed.");
        }
    }

    /**
     * The main method to run the chat client.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        new ChatClient("localhost", Constants.SERVER_PORT);
    }
}
