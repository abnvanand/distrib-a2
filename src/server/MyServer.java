package server;

import common.MyStreamSocket;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import static common.Constants.UPLOAD_PATH;

public class MyServer {
    private static HashSet<String> userNames;
    private static HashSet<String> groups;
    // <Groupname, Users>
    private static ConcurrentHashMap<String, HashSet<String>> groupUsersMapping;
    // <Username, filepaths>
    private static ConcurrentHashMap<String, HashSet<String>> userFilesMapping;

    public static void main(String[] args) {
        int serverPort = 12345;
        userNames = new HashSet<>();
        groups = new HashSet<>();
        groupUsersMapping = new ConcurrentHashMap<>();
        userFilesMapping = new ConcurrentHashMap<>();

        try {
            ServerSocket myConnectionSocket = new ServerSocket(serverPort);
            System.out.println("Server is running at: " + serverPort);

            // Loop forever
            while (true) {
                // Wait for a connection
                System.out.println("Waiting for a connection");
                MyStreamSocket dataSocket = new MyStreamSocket
                        (myConnectionSocket.accept()); // Accept() is a blocking call
                System.out.println("Connection accepted");
                // Start a thread to handle this client's session
                Thread theThread = new Thread(new MyServerThread(dataSocket));
                theThread.start();
                // Now loop to next client

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getFile(MyStreamSocket dataSocket)
            throws IOException {
        String groupName = dataSocket.receiveMessage();
        String userName = dataSocket.receiveMessage();
        String filePath = dataSocket.receiveMessage();
        if (!groups.contains(groupName)) {
            System.out.println("Group " + groupName + " does not exist.");
            dataSocket.sendMessage("error");
            return;
        }
        if (!userNames.contains(userName)) {
            System.out.println("User " + userName + " does not exist.");
            dataSocket.sendMessage("error");
            return;
        }
        String fullyQualifiedPath = String.format("%s/%s/%s", UPLOAD_PATH, userName, filePath);
        System.out.println("Sending file: " + fullyQualifiedPath);
        File file = new File(fullyQualifiedPath);
        dataSocket.sendMessage(String.valueOf(file.length()));
        dataSocket.sendFile(fullyQualifiedPath);
    }

    public static void listDetail(MyStreamSocket dataSocket)
            throws IOException {
        HashMap<String, HashSet<String>> filteredUserFiles = new HashMap<>();

        String groupName = dataSocket.receiveMessage();
        if (!groups.contains(groupName)) {
//         TODO: send error back to client or empty list
            dataSocket.sendMessage(filteredUserFiles.toString());
            return;
        }
        HashSet<String> userNames = groupUsersMapping.getOrDefault(groupName, new HashSet<>());
        for (String userName : userNames) {
            System.out.println("Username :" + userName);
            HashSet<String> filePaths = userFilesMapping.getOrDefault(userName, new HashSet<>());
            System.out.println("\t" + filePaths);
            filteredUserFiles.put(userName, filePaths);
        }
        dataSocket.sendMessage(filteredUserFiles.toString());
    }

    public static void leaveGroup(MyStreamSocket dataSocket)
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

    public static void joinGroup(MyStreamSocket dataSocket)
            throws IOException {
        String userName = dataSocket.receiveMessage();
        String groupName = dataSocket.receiveMessage();
        if (!userNames.contains(userName)) {
            dataSocket.sendMessage("Error: invalid username " + userName);
            return;
        }
        if (!groups.contains(groupName)) {
            dataSocket.sendMessage("Error: invalid group name " + groupName);
            return;
        }
        System.out.println("groupUsersMapping before: " + groupUsersMapping);
        groupUsersMapping.putIfAbsent(groupName, new HashSet<>());
        groupUsersMapping.get(groupName).add(userName);
        System.out.println("groupUsersMapping after: " + groupUsersMapping);

        dataSocket.sendMessage("User: " + userName + " added to Group: " + groupName);
    }

    public static void listGroups(MyStreamSocket dataSocket) throws IOException {
        dataSocket.sendMessage(groups.toString());
    }

    public static void createGroup(MyStreamSocket dataSocket) throws IOException {
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

    public static void moveFile(MyStreamSocket dataSocket) throws IOException {
        String userName = dataSocket.receiveMessage();  // needs username to update userFilesMapping
        String source = dataSocket.receiveMessage();
        String destination = dataSocket.receiveMessage();

        Path temp = null;
        try {
            temp = Files.move
                    (Paths.get(String.format("%s/%s/%s", UPLOAD_PATH, userName, source)),
                            Paths.get(String.format("%s/%s/%s", UPLOAD_PATH, userName, destination)));
        } catch (IOException e) {
            // TODO: make error msg more user friendly
            e.printStackTrace();
        }

        if (temp != null) {
            userFilesMapping.get(userName).remove(source);
            userFilesMapping.get(userName).add(destination);

            System.out.println("File renamed and moved successfully");
            dataSocket.sendMessage("Success");
        } else {
            System.out.println("Failed to move the file");
            dataSocket.sendMessage("Failed");
        }
    }

    public static void createFolder(MyStreamSocket dataSocket) throws IOException {
        String userName = dataSocket.receiveMessage();
        String folderName = dataSocket.receiveMessage();
        File dir = new File(String.format("%s/%s/%s", UPLOAD_PATH, userName, folderName));

        // attempt to create the directory here
        boolean success = dir.mkdir();
        if (success) {
            // creating the directory succeeded
            userFilesMapping.putIfAbsent(userName, new HashSet<>());
            userFilesMapping.get(userName).add(folderName);

            System.out.println("Directory created successfully");
            dataSocket.sendMessage("Success");
        } else {
            // creating the directory failed
            System.out.println("Directory creation failed");
            dataSocket.sendMessage("Failed");
        }
    }

    public static void uploadFile(MyStreamSocket dataSocket)
            throws IOException {
        String userName = dataSocket.receiveMessage();
        String fileName = dataSocket.receiveMessage();  // filename with which it will be saved on the server
        String fileSize = dataSocket.receiveMessage();

        String fullPath = String.format("%s/%s/%s", UPLOAD_PATH, userName, fileName);
        System.out.println("Uploading file to: " + fullPath);

        dataSocket.receiveFile(fullPath, Long.parseLong(fileSize));

        userFilesMapping.putIfAbsent(userName, new HashSet<>());
        userFilesMapping.get(userName).add(fileName);

        System.out.println("Uploaded file to: " + fullPath);
    }

    public static void createUser(MyStreamSocket dataSocket)
            throws IOException {
        System.out.println("Create user request received.");
        String newUser = dataSocket.receiveMessage();
        if (userNames.contains(newUser)) {
            System.out.println("User exists");
            dataSocket.sendMessage("Error");
        } else {
            userNames.add(newUser);
            // Create a user folder
            File dir = new File(String.format("%s/%s/", UPLOAD_PATH, newUser));
            if (dir.mkdir()) {
                System.out.println("Root directory for user " + newUser + " created successfully");
            } else {
                System.out.println("Root directory for user " + newUser + " could not be created.");
            }

            System.out.println("User created: " + newUser);
            dataSocket.sendMessage("Success: user " + newUser + " created successfully.");
        }
    }
}
