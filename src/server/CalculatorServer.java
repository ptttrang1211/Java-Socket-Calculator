package server;

import java.io.IOException;
import java.net.*;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.*;

public class CalculatorServer {
    private static final String DEFAULT_IP = "localhost";   // Default IP address
    private static final int DEFAULT_PORT = 1234;           // Default port (used if config file fails)
    private static int PORT;                                 // Actual port used by the server
    private static ExecutorService threadPool = Executors.newFixedThreadPool(10); // Create a thread pool with 10 threads
    private static final String CONFIG_PATH = "src/server/server_info.dat";        // Path to server configuration file

    public static void main(String[] args) {

        // --- Determine the PORT (read from file or use default) ---
        try {
            List<String> lines = Files.readAllLines(Paths.get(CONFIG_PATH));       // Read all lines from the configuration file
            if (lines.size() >= 2) {                                              // If the file has at least 2 lines
                PORT = Integer.parseInt(lines.get(1).trim());                     // Use the second line as the port number
            } else {
                PORT = DEFAULT_PORT;                                              // Otherwise, use the default port
            }
        } catch (IOException | NumberFormatException e) {                         // Handle file reading or parsing errors
            PORT = DEFAULT_PORT;                                                  // Fall back to the default port
            System.err.println("⚠️ Warning: Failed to read configuration file. Using default port: " + PORT); // Show warning
        }

        // ---  Start the server with the chosen PORT ---
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {                // Create a server socket that listens on the port
            System.out.println("Server is listening on port " + PORT);            // Display the active port
            System.out.println("Waiting for client connections...");              // Inform user that the server is waiting for clients

            while (true) {                                                        // Infinite loop to handle multiple clients
                Socket clientSocket = serverSocket.accept();                      // Wait for a client to connect
                System.out.println("Client connected: " + clientSocket.getRemoteSocketAddress()); // Print client info

                threadPool.execute(new ClientHandler(clientSocket));              // Handle client using a separate thread
            }

        } catch (IOException e) {                                                 // Handle server socket-related errors
            System.out.println(" Critical Server Socket Error: " + e.getMessage()); // Print error message
        } finally {
            if (threadPool != null && !threadPool.isShutdown()) {                 // When server stops, ensure thread pool is closed
                threadPool.shutdown();                                            // Shut down thread pool gracefully
            }
        }
    }
}
