package test;

import client.CalculatorClient;
import org.junit.jupiter.api.*;
import java.io.*;
import java.net.*;

import static org.junit.jupiter.api.Assertions.*;

public class CalculatorClientTest {

    private static ServerSocket mockServer;
    private static Thread serverThread;

    @BeforeAll
    static void startMockServer() throws Exception {
        mockServer = new ServerSocket(7777);

        // Server giả chạy song song
        serverThread = new Thread(() -> {
            try {
                while (true) {
                    Socket socket = mockServer.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    String line = in.readLine();
                    if (line != null && line.contains("ADD")) {
                        out.println("ANSWER:15.0");
                    } else {
                        out.println("ERROR:COMMAND:Unknown");
                    }
                    socket.close();
                }
            } catch (IOException ignored) {}
        });
        serverThread.start();
    }

    @AfterAll
    static void stopMockServer() throws Exception {
        mockServer.close();
    }

    @Test
    void testAddition() throws Exception {
        CalculatorClient client = new CalculatorClient("src/server/server_info.dat");
        String response = client.sendCalculation("ADD", "10", "5");
        assertEquals("ANSWER:15.0", response);
        client.close();
    }

    @Test
    void testUnknownCommand() throws Exception {
        CalculatorClient client = new CalculatorClient("src/server/server_info.dat");
        String response = client.sendCalculation("XYZ", "3", "2");
        assertTrue(response.startsWith("ERROR"));
        client.close();
    }
}
