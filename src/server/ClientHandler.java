package server;
import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {       // Class to handle each client connection in a separate thread
    private Socket clientSocket;                       // Socket object connected client
    public ClientHandler(Socket clientSocket) {        // Constructor
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {                                // Method thread starts (from Runnable)
        long threadId = Thread.currentThread().getId();                // Get current thread ID
        String threadInfo = "[Thread-" + threadId + "]";               // Format thread info string
        // IP address + port
        System.out.println(threadInfo + " Client connected from: " + clientSocket.getRemoteSocketAddress());

        try (
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // For reading client messages
                PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream(), true)                            // For sending responses to client
        ) {
            String inputLine;                                         // incoming line from client

            while ((inputLine = bufferedReader.readLine()) != null) { // Loop to handle multiple requests from same client
                System.out.println(threadInfo + " Received request: " + inputLine); // Log received message
                String[] tokens = inputLine.split(" ");               // Split message by space → expect 3 parts

                // Check syntax (must have exactly 3 tokens)
                if (tokens.length != 3) {
                    String msg = "ERROR:SYNTAX_01:Invalid command format (Expected: CMD OP1 OP2)";
                    printWriter.println(msg);
                    System.out.println(threadInfo + " → Sent response: " + msg); // Log the response
                    continue;
                }

                String op = tokens[0].toUpperCase();                   // Convert operation name to uppercase (ADD, SUB, etc.)
                double a, b;
                String response;

                //  Check if operands are valid numbers
                try {
                    a = Double.parseDouble(tokens[1]);                 // Convert first operand
                    b = Double.parseDouble(tokens[2]);                 // Convert second operand
                } catch (NumberFormatException e) {                    // If not valid numbers
                    String msg = "ERROR:SYNTAX_03:Operands must be valid numbers";
                    printWriter.println(msg);
                    System.out.println(threadInfo + " → Sent response: " + msg); // Log
                    continue;
                }

                double result;

                // Perform operation based on command
                switch (op) {
                    case "ADD":                                        // Addition
                        result = a + b;
                        response = "ANSWER:" + result;                 // Format response message
                        break;
                    case "SUB":                                        // Subtraction
                        result = a - b;
                        response = "ANSWER:" + result;
                        break;
                    case "MUL":                                        // Multiplication
                        result = a * b;
                        response = "ANSWER:" + result;
                        break;
                    case "DIV":                                        // Division
                        if (b == 0) {                                  // Division by zero check
                            response = "ERROR:RUNTIME_01:Division by zero";
                            printWriter.println(response);
                            System.out.println(threadInfo + " → Sent response: " + response); // Log
                            continue;
                        }
                        result = a / b;                                // Perform division
                        response = "ANSWER:" + result;
                        break;
                    default:                                            // If command not recognized
                        response = "ERROR:COMMAND_01:Unknown operation (" + op + ")"; // Unknown operation error
                        printWriter.println(response);
                        System.out.println(threadInfo + " → Sent response: " + response); // Log
                        continue;
                }

                // Send successful result back to client
                printWriter.println(response);                         // Send final answer
                System.out.println(threadInfo + " → Sent response: " + response); // Log the sent result
            }

        } catch (IOException e) {                                      // Handle connection interruption
            System.err.println(threadInfo + " Client disconnected (" + e.getMessage() + ") from: " + clientSocket.getRemoteSocketAddress());
            // Log disconnection info and cause
        } finally {
            System.out.println(threadInfo + " Finished handling client."); // Always executed → log end of thread
        }
    }
}
