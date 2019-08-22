package server;

import common.Constants;
import common.MyStreamSocket;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;

public class MyServer {
    private static HashSet<String> userNames;
    private static HashSet<String> groups;
    // <Groupname, Users>
    private static HashMap<String, HashSet<String>> groupUsersMapping;

    public static void main(String[] args) {
        int serverPort = 12345;
        userNames = new HashSet<>();
        groups = new HashSet<>();
        groupUsersMapping = new HashMap<>();

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
                } else if (Constants.MessageTypes.MOVE_FILE.equals(msgType)) {
                    moveFile(dataSocket);
                } else if (Constants.MessageTypes.CREATE_GROUP.equals(msgType)) {
                    createGroup(dataSocket);
                } else if (Constants.MessageTypes.LIST_GROUPS.equals(msgType)) {
                    listGroups(dataSocket);
                } else if (Constants.MessageTypes.JOIN_GROUP.equals(msgType)) {
                    joinGroup(dataSocket);
                } else if (Constants.MessageTypes.LEAVE_GROUP.equals(msgType)) {
                    leaveGroup(dataSocket);
                }
                dataSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void leaveGroup(MyStreamSocket dataSocket)
            throws IOException {
        String userName = dataSocket.receiveMessage();
        String groupName = dataSocket.receiveMessage();
        if (!userNames.contains(userName)) {
            dataSocket.sendMessage("Error: invalid username");
            return;
        }
        if (!groups.contains(groupName)) {
            dataSocket.sendMessage("Error: invalid group name");
            return;
        }

        System.out.println("groupUsersMapping before: " + groupUsersMapping);
        // FIXME: possible exception if user is not already in the group
        groupUsersMapping.getOrDefault(groupName, new HashSet<>())
                .remove(userName);
        System.out.println("groupUsersMapping after: " + groupUsersMapping);

        dataSocket.sendMessage("User: " + userName + " removed from Group: " + groupName);
    }

    private static void joinGroup(MyStreamSocket dataSocket)
            throws IOException {
        String userName = dataSocket.receiveMessage();
        String groupName = dataSocket.receiveMessage();
        if (!userNames.contains(userName)) {
            dataSocket.sendMessage("Error: invalid username");
            return;
        }
        if (!groups.contains(groupName)) {
            dataSocket.sendMessage("Error: invalid group name");
            return;
        }
        System.out.println("groupUsersMapping before: " + groupUsersMapping);
        groupUsersMapping.putIfAbsent(groupName, new HashSet<>());
        groupUsersMapping.get(groupName).add(userName);
        System.out.println("groupUsersMapping after: " + groupUsersMapping);

        dataSocket.sendMessage("User: " + userName + " added to Group: " + groupName);
    }

    private static void listGroups(MyStreamSocket dataSocket) throws IOException {
        dataSocket.sendObject(groups);
    }

    private static void createGroup(MyStreamSocket dataSocket) throws IOException {
        String groupName = dataSocket.receiveMessage();
        if (groups.contains(groupName)) {
            System.out.println("Group " + groupName + " already exists.");
            dataSocket.sendMessage("Error: group already exists");
        } else {
            groups.add(groupName);
            System.out.println("Group " + groupName + " created successfully.");
            dataSocket.sendMessage("Success");
        }
    }

    private static void moveFile(MyStreamSocket dataSocket) throws IOException {
        // TODO: Do without nio?
        String source = dataSocket.receiveMessage();
        String destination = dataSocket.receiveMessage();

        Path temp = null;
        try {
            temp = Files.move
                    (Paths.get(source),
                            Paths.get(destination));
        } catch (IOException e) {
            // TODO: make error msg more user freindly
            e.printStackTrace();
        }

        if (temp != null) {
            System.out.println("File renamed and moved successfully");
            dataSocket.sendMessage("Success");
        } else {
            System.out.println("Failed to move the file");
            dataSocket.sendMessage("Failed");
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
