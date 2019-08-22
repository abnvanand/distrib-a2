package server;

import common.Constants;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

public class MyServer {
    static HashSet<String> userNames;

    public static void main(String[] args) {
        int serverPort = 12345;
        userNames = new HashSet<>();

        try {
            ServerSocket myConnectionSocket = new ServerSocket(serverPort);
            System.out.println("Server is ready");

            while (true) {
                // Wait for a connection
                System.out.println("Waiting for a connection");
                Socket dataSocket = myConnectionSocket.accept();    // Accept() is a blocking call
                System.out.println("Connection accepted");

                OutputStream outputStream = dataSocket.getOutputStream();
                PrintWriter output = new PrintWriter(new OutputStreamWriter(outputStream));

                InputStream inputStream = dataSocket.getInputStream();
                BufferedReader input = new BufferedReader(new InputStreamReader(inputStream));

                // Message is received in the form of multiple lines
                // First line specifies message type
                String msgType = input.readLine();
                System.out.println(" input.readLine(): " + msgType);
                if (Constants.MessageTypes.CREATE_USER.equals(msgType)) {
                    createUser(output, input);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createUser(PrintWriter output, BufferedReader input) throws IOException {
        System.out.println("Create user request received.");
        String newUser = input.readLine();
        if (userNames.contains(newUser)) {
            System.out.println("User exists");
            output.println("Error: user already exists.");
            output.flush();
        } else {
            userNames.add(newUser);
            System.out.println("User created: " + newUser);
            output.println("Success: user " + newUser + " created successfully.");
            output.flush();
        }
    }
}
