package server;

import common.Constants;
import common.MyStreamSocket;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashSet;

public class MyServer {
    private static HashSet<String> userNames;

    public static void main(String[] args) {
        int serverPort = 12345;
        userNames = new HashSet<>();

        try {
            ServerSocket myConnectionSocket = new ServerSocket(serverPort);
            System.out.println("Server is ready");

            while (true) {
                // Wait for a connection
                System.out.println("Waiting for a connection");
                MyStreamSocket dataSocket = new MyStreamSocket(myConnectionSocket.accept()); // Accept() is a blocking call
                System.out.println("Connection accepted");

                // Message is received in the form of multiple lines
                // First line specifies message type
                String msgType = dataSocket.receiveMessage();
                System.out.println(" input.readLine(): " + msgType);
                if (Constants.MessageTypes.CREATE_USER.equals(msgType)) {
                    createUser(dataSocket);
                } else if (Constants.MessageTypes.UPLOAD_FILE.equals(msgType)) {
                    uploadFile(dataSocket);
                } else if (Constants.MessageTypes.CREATE_FOLDER.equals(msgType)) {
                    createFolder(dataSocket);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createFolder(MyStreamSocket dataSocket) throws IOException {
        String folderName = dataSocket.receiveMessage();
        File dir = new File(folderName);

        // attempt to create the directory here
        boolean success = dir.mkdir();
        if (success) {
            // creating the directory succeeded
            System.out.println("Directory created successfully");
            dataSocket.sendMessage("Success");
        } else {
            // creating the directory failed
            System.out.println("Directory creation failed");
            dataSocket.sendMessage("Failed");
        }
    }

    private static void uploadFile(MyStreamSocket dataSocket)
            throws IOException {
        // TODO: Accept file name from client
        dataSocket.receiveFile("newfile.mp4");
    }

    private static void createUser(MyStreamSocket dataSocket) throws IOException {
        System.out.println("Create user request received.");
        String newUser = dataSocket.receiveMessage();
        if (userNames.contains(newUser)) {
            System.out.println("User exists");
            dataSocket.sendMessage("Error: user already exists.");
        } else {
            userNames.add(newUser);
            System.out.println("User created: " + newUser);
            dataSocket.sendMessage("Success: user " + newUser + " created successfully.");
        }
    }
}
