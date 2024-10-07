// File: Client/src/main/java/edu/sdccd/cisc191/client/Client.java
package edu.sdccd.cisc191.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.sdccd.cisc191.common.Message;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Client {
    private String host;
    private int port;
    private BufferedWriter out;
    private BufferedReader in;
    private ObjectMapper objectMapper;
    private Socket socket;

    /**
     * Primary constructor used in production.
     *
     * @param host The server host.
     * @param port The server port.
     * @throws IOException If an I/O error occurs when creating the socket.
     */
    public Client(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        this.objectMapper = new ObjectMapper();
        this.socket = new Socket(host, port);
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.in = new BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));
    }

    /**
     * Secondary constructor used for testing with injected dependencies.
     *
     * @param out          The BufferedWriter to send messages.
     * @param in           The BufferedReader to receive messages.
     * @param objectMapper The ObjectMapper for JSON processing.
     */
    public Client(BufferedWriter out, BufferedReader in, ObjectMapper objectMapper) {
        this.out = out;
        this.in = in;
        this.objectMapper = objectMapper;
    }

    /**
     * Sends a message to the server.
     *
     * @param message The message to send.
     * @throws IOException If an I/O error occurs during sending.
     */
    public void sendMessage(Message message) throws IOException {
        String json = objectMapper.writeValueAsString(message);
        out.write(json);
        out.newLine();
        out.flush();
    }

    /**
     * Receives a message from the server.
     *
     * @return The received Message object, or null if no message is received.
     * @throws IOException If an I/O error occurs during receiving.
     */
    public Message receiveMessage() throws IOException {
        String json = in.readLine();
        if (json == null || json.isEmpty()) {
            return null;
        }
        return objectMapper.readValue(json, Message.class);
    }

    /**
     * Closes the client connection.
     *
     * @throws IOException If an I/O error occurs during closing.
     */
    public void close() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        if (out != null) {
            out.close();
        }
        if (in != null) {
            in.close();
        }
    }

    // Getters for testing purposes
    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
