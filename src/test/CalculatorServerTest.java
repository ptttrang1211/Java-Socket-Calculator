package test;

import org.junit.jupiter.api.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

public class CalculatorServerTest {

    private static ExecutorService serverThread;

    @BeforeAll
    static void startServer() {
        serverThread = Executors.newSingleThreadExecutor();
        serverThread.submit(() -> {
            try {
                server.CalculatorServer.main(null); // chạy server thật
            } catch (Exception ignored) {}
        });

        // Chờ server mở port
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
    }

    @AfterAll
    static void stopServer() {
        serverThread.shutdownNow();
    }
    private String send(String msg) throws IOException {
        try (Socket s = new Socket("127.0.0.1", 7777);
             BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
             PrintWriter out = new PrintWriter(s.getOutputStream(), true)) {
            out.println(msg);
            return in.readLine();
        }
    }

    @Test
    void testAddition() throws IOException {
        String res = send("ADD 3 7");
        assertEquals("ANSWER:10.0", res);
    }

    @Test
    void testDivisionByZero() throws IOException {
        String res = send("DIV 8 0");
        assertTrue(res.contains("Division by zero"));
    }

    @Test
    void testInvalidCommand() throws IOException {
        String res = send("XYZ 1 2");
        assertTrue(res.startsWith("ERROR:COMMAND"));
    }

    @Test
    void testInvalidFormat() throws IOException {
        String res = send("ADD 5");
        assertTrue(res.startsWith("ERROR:SYNTAX"));
    }
}
