package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class CalculatorClient {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 1234;

    private Socket socket;                                   // Socket object
    private PrintWriter printWriter;                         // To send data (requests) to the server
    private BufferedReader bufferedReader;                   // To read responses from the server
    private String connectedHost;                            // Stores the host actually connected to
    private int connectedPort;                               // Stores the port actually connected to

    public CalculatorClient(String configFile) throws IOException {
        String host = DEFAULT_HOST;
        int port = DEFAULT_PORT;

        // ---  LOAD CONFIGURATION OR USE DEFAULT VALUES ---
        try {
            List<String> lines = Files.readAllLines(Paths.get(configFile));   // Read all lines  config file

            if (!lines.isEmpty()) {                       // If file not empty
                host = lines.get(0).trim();               // First line = host (IP address)
                if (lines.size() >= 2) {                  // Second line = port (if exists)
                    port = Integer.parseInt(lines.get(1).trim());
                }
            }
        } catch (IOException | NumberFormatException e) { // Handle file read or parsing errors
            System.err.println("️ Config file read error: " + e.getMessage() +
                    ". Using default: " + host + ":" + port);
        }

        // ---  ESTABLISH CONNECTION WITH SERVER ---
        System.out.println("[Client] Connecting to: " + host + ":" + port);
        socket = new Socket(host, port);                   // Create socket connection server

        // Connection success log
        this.connectedHost = host;
        this.connectedPort = port;
        System.out.println("[Client] Connected successfully.");

        // Initialize I/O streams
        printWriter = new PrintWriter(socket.getOutputStream(), true);       // Output stream (auto-flush = true)
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Input stream
    }

    // ---  METHOD TO SEND A CALCULATION REQUEST ---
    public String sendCalculation(String op, String a, String b) throws IOException {
        String request = op + " " + a + " " + b;           // Format message: COMMAND operand1 operand2

        System.out.println("[Client Send] Sending request: " + request);
        printWriter.println(request);

        String response = bufferedReader.readLine();       // Wait and read server response
        System.out.println("[Client Receive] Received response: " + response);

        return response;
    }

    // --- METHOD TO CLOSE CONNECTION ---
    public void close() {
        try {
            if (socket != null) {
                socket.close();
                System.out.println("[Client] Connection closed with " +
                        connectedHost + ":" + connectedPort);
            }
        } catch (IOException e) {
            System.err.println("Client close error: " + e.getMessage()); // Log closing errors
        }
    }

    // --- MAIN METHOD (For test or GUI integration) ---
    public static void main(String[] args) {
        // The GUI (CalculatorGUI.java) will create and manage the client object.
    }
}
