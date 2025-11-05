package client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.*;
import java.io.IOException;

public class CalculatorGUI extends JFrame implements ActionListener {
    private static final String DEFAULT_IP = "localhost";
    private static final int DEFAULT_PORT = 1234;

    private JTextField tfA, tfB, tfRes, tfServer, tfPort;      // Input/output text fields
    private JButton btnAdd, btnSub, btnMul, btnDiv;            // Operation buttons

    private String serverIP;
    private int serverPort;

    private CalculatorClient client;                           // Client object for communication with server

    public CalculatorGUI() {                                   // Constructor (GUI setup)
        setTitle("Calculator Client");
        setDefaultCloseOperation(EXIT_ON_CLOSE);               // Exit program when window closes
        setSize(550, 260);                                     // Set window size
        setResizable(false);                                   // Prevent window resizing
        setLocationRelativeTo(null);                           // Center window on screen

        readServerInfo();                                      // Load server IP and port from configuration file

        // ---  Initialize the client and handle connection errors ---
        try {
            client = new CalculatorClient("src/server/server_info.dat"); // Try connecting to server
        } catch (IOException e) {
            client = null;                                      // Nullify client if connection fails
            JOptionPane.showMessageDialog(this,
                    "Initial connection failed (" + serverIP + ":" + serverPort + "): " + e.getMessage(),
                    "Connection Error", JOptionPane.ERROR_MESSAGE); // Show error popup
        }

        // ---⃣ Build GUI Layout using Swing Boxes ---
        Box mainBox = Box.createVerticalBox();                  // Main container (vertical layout)
        Box b1 = Box.createHorizontalBox();                     // Line for Number A
        Box b2 = Box.createHorizontalBox();                     // Line for Number B
        Box b3 = Box.createHorizontalBox();                     // Line for buttons
        Box b4 = Box.createHorizontalBox();                     // Line for port
        Box b5 = Box.createHorizontalBox();                     // Line for server IP
        Box b6 = Box.createHorizontalBox();                     // Line for result display

        JLabel lblServer, lblPort, lblA, lblB, lblRes;          // Labels for each field

        // --- Server IP Box ---
        lblServer = new JLabel("Server IP:", JLabel.RIGHT);
        tfServer = new JTextField(serverIP);
        tfServer.setEditable(false);                            // Make read-only
        b5.add(lblServer);
        b5.add(tfServer);

        // --- Port Box ---
        lblPort = new JLabel("Port:", JLabel.RIGHT);
        tfPort = new JTextField(String.valueOf(serverPort));
        tfPort.setEditable(false);                              // Make read-only
        b4.add(lblPort);
        b4.add(tfPort);

        // --- Input A Box ---
        lblA = new JLabel("Num A:", JLabel.RIGHT);
        tfA = new JTextField();
        b1.add(lblA);
        b1.add(tfA);

        // --- Input B Box ---
        lblB = new JLabel("Num B:", JLabel.RIGHT);
        tfB = new JTextField();
        b2.add(lblB);
        b2.add(tfB);

        // --- Operation Buttons ---
        btnAdd = new JButton("ADD (+)");
        btnSub = new JButton("SUB (-)");
        btnMul = new JButton("MUL (*)");
        btnDiv = new JButton("DIV (/)");

        // Disable buttons if server connection fails
        if (client == null) {
            btnAdd.setEnabled(false);
            btnSub.setEnabled(false);
            btnMul.setEnabled(false);
            btnDiv.setEnabled(false);
        }

        b3.add(btnAdd);
        b3.add(btnSub);
        b3.add(btnMul);
        b3.add(btnDiv);
        b3.add(Box.createHorizontalGlue());

        // --- Result Box ---
        tfRes = new JTextField();
        tfRes.setEditable(false);                               // Result field is not editable
        lblRes = new JLabel("Result:", JLabel.RIGHT);
        b6.add(lblRes);
        b6.add(tfRes);

        // Adjust label sizes for neat alignment
        lblA.setPreferredSize(lblServer.getPreferredSize());
        lblB.setPreferredSize(lblServer.getPreferredSize());
        lblPort.setPreferredSize(lblServer.getPreferredSize());
        lblRes.setPreferredSize(lblServer.getPreferredSize());

        // Register ActionListeners for buttons
        btnAdd.addActionListener(this);
        btnSub.addActionListener(this);
        btnMul.addActionListener(this);
        btnDiv.addActionListener(this);

        // Add all boxes into mainBox with spacing
        mainBox.add(Box.createVerticalStrut(10));
        mainBox.add(b5);
        mainBox.add(Box.createVerticalStrut(5));
        mainBox.add(b4);
        mainBox.add(Box.createVerticalStrut(5));
        mainBox.add(b1);
        mainBox.add(Box.createVerticalStrut(5));
        mainBox.add(b2);
        mainBox.add(Box.createVerticalStrut(10));
        mainBox.add(b3);
        mainBox.add(Box.createVerticalStrut(10));
        mainBox.add(b6);
        mainBox.add(Box.createVerticalStrut(10));

        this.add(mainBox, BorderLayout.NORTH);                   // Add GUI layout to frame

        // --- Close server connection when window closes ---
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (client != null) client.close();              // Close client connection before exiting
                System.exit(0);
            }
        });
    }

    // ---  Read server info from configuration file ---
    private void readServerInfo() {
        try (BufferedReader br = new BufferedReader(new FileReader("src/server/server_info.dat"))) {
            serverIP = br.readLine().trim();                     // First line = IP
            serverPort = Integer.parseInt(br.readLine().trim()); // Second line = Port
        } catch (Exception e) {
            serverIP = DEFAULT_IP;                               // Use default if read fails
            serverPort = DEFAULT_PORT;
            System.err.println("⚠️ Warning: Config read error. Using default: " + serverIP + ":" + serverPort);
        }
    }

    // ---  Handle button click events ---
    @Override
    public void actionPerformed(ActionEvent e) {
        if (client == null) {                                   // If no client connection
            JOptionPane.showMessageDialog(this, "No server connection. Please restart.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Object src = e.getSource();                             // Identify which button was clicked
        String op = "";

        if (src.equals(btnAdd)) op = "ADD";                     // Addition +
        else if (src.equals(btnSub)) op = "SUB";                // Subtraction -
        else if (src.equals(btnMul)) op = "MUL";                // Multiplication *
        else if (src.equals(btnDiv)) op = "DIV";                // Division /

        double a, b;                                            // Input numbers

        // Validate user input
        try {
            a = Double.parseDouble(tfA.getText());              // input A
            b = Double.parseDouble(tfB.getText());              // input B
        } catch (NumberFormatException ex) {
            System.err.println("[CLIENT LOG] Invalid input: " + tfA.getText() + ", " + tfB.getText()); // Local log
            try {
                client.sendCalculation("ERROR:CLIENT_INPUT", tfA.getText(), tfB.getText()); // Optional: send log to server
            } catch (Exception ignored) {
                System.err.println("Error sending log to server.");
            }
            JOptionPane.showMessageDialog(this, "Error: Invalid number input."); // Alert user
            return;
        }

        // --- ⃣ Communicate with server ---
        try {
            String response = client.sendCalculation(op, String.valueOf(a), String.valueOf(b)); // Send request via client

            if (response != null) {
                if (response.startsWith("ANSWER:")) {           // Successful response
                    tfRes.setText(response.substring(7));       // Display numeric result
                } else if (response.startsWith("ERROR:")) {     // Error from server
                    tfRes.setText(response);
                    JOptionPane.showMessageDialog(this, response, "Server Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    tfRes.setText("Unrecognized response: " + response); // Unexpected format
                }
            } else {
                tfRes.setText("No response from server.");      // Null response
            }

        } catch (IOException ex) {                              // Connection lost or communication error
            JOptionPane.showMessageDialog(this, "Server communication error: " + ex.getMessage());
            tfRes.setText("Communication error.");
            if (client != null) client.close();                 // Close connection if broken
            client = null;                                      // Set client to null to disable further actions
        }
    }

    // --- 6️⃣ Main method to launch the GUI ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {                      // Start GUI safely on Event Dispatch Thread
            new CalculatorGUI().setVisible(true);               // Create and display GUI window
        });
    }
}
