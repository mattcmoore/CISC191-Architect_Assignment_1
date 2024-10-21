// File: ClientTest.java
package edu.sdccd.cisc191.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.sdccd.cisc191.common.Message;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {
    private PipedInputStream clientInputPipe;
    private PipedOutputStream clientOutputPipe;
    private PipedInputStream serverInputPipe;
    private PipedOutputStream serverOutputPipe;
    private BufferedWriter serverWriter;
    private BufferedReader serverReader;
    private Client client;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws IOException {
        // Initialize ObjectMapper
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Create piped streams to simulate server-client communication
        // Client's output is server's input
        clientOutputPipe = new PipedOutputStream();
        serverInputPipe = new PipedInputStream(clientOutputPipe);

        // Server's output is client's input
        serverOutputPipe = new PipedOutputStream();
        clientInputPipe = new PipedInputStream(serverOutputPipe);

        // Initialize server's writer and reader
        serverWriter = new BufferedWriter(new OutputStreamWriter(serverOutputPipe));
        serverReader = new BufferedReader(new InputStreamReader(serverInputPipe));

        // Initialize the Client with piped streams
        BufferedWriter clientWriter = new BufferedWriter(new OutputStreamWriter(clientOutputPipe));
        BufferedReader clientReader = new BufferedReader(new InputStreamReader(clientInputPipe));
        client = new Client(clientWriter, clientReader, objectMapper);
    }

    @AfterEach
    void tearDown() throws IOException {
        if (client != null) {
            client.close();
        }
        if (serverWriter != null) {
            serverWriter.close();
        }
        if (serverReader != null) {
            serverReader.close();
        }
    }

    @Test
    void testSendMessage() throws IOException {
        // Prepare a message
        Message message = new Message("TEST_ACTION", "Test Data");
        String expectedJson = objectMapper.writeValueAsString(message);

        // Send the message
        client.sendMessage(message);

        // Read what was sent from the client's output (server's input)
        String actualJson = serverReader.readLine();

        assertEquals(expectedJson, actualJson, "The sent JSON should match the expected JSON");
    }

    @Test
    void testReceiveEmptyMessage() throws IOException {

        // Simulate the server sending an empty message
        serverWriter.write("");
        serverWriter.newLine();
        serverWriter.flush();

        // Call the method that handles receiving a message
        Message receivedMessage = client.receiveMessage();

        // Check if it handles the empty message gracefully
        assertNull(receivedMessage, "Received message should be null for empty input");
    }

    @Test
    void testClientIsClosed() throws IOException {
        // Close the client
        client.close();

        // Expect an IOException when trying to send a message after close
        IOException sendException = assertThrows(IOException.class, () -> {
            client.sendMessage(new Message("TEST_ACTION", "Test after close"));
        }, "Sending a message after closing the client should throw IOException");
        assertTrue(sendException.getMessage().contains("Stream closed") || sendException.getMessage().isEmpty(),
                "IOException should indicate stream closure");

        // Expect an IOException when trying to receive a message after close
        IOException receiveException = assertThrows(IOException.class, () -> {
            client.receiveMessage();
        }, "Receiving a message after closing the client should throw IOException");
        assertTrue(receiveException.getMessage().contains("Stream closed") || receiveException.getMessage().isEmpty(),
                "IOException should indicate stream closure");
    }

    @Test
    void testReceiveMessage() throws IOException {

        /** Proves network communication from server to client, a given message gets a certain response from server */

        // Prepare a message to simulate receiving from the server
        Message expectedMessage = new Message("TEST_ACTION", "Test Data");
        String jsonResponse = objectMapper.writeValueAsString(expectedMessage);

        // Simulate the server sending the message
        serverWriter.write(jsonResponse);
        serverWriter.newLine();
        serverWriter.flush();

        // Call the method that handles receiving a message
        Message receivedMessage = client.receiveMessage();

        assertNotNull(receivedMessage, "Received message should not be null");
        assertEquals(expectedMessage.getAction(), receivedMessage.getAction(), "Action should match");
        assertEquals(expectedMessage.getData(), receivedMessage.getData(), "Data should match");
    }

    @Test
    void testSendMessageWithProperConnection() throws IOException {
        // Ensure the client can send a message correctly
        Message message = new Message("TEST_ACTION", "Test Data");
        String expectedJson = objectMapper.writeValueAsString(message);

        // Send the message
        client.sendMessage(message);

        // Read the sent message from the client's output (server's input)
        String actualJson = serverReader.readLine();

        assertEquals(expectedJson, actualJson, "The sent JSON should match the expected JSON");
    }
}
