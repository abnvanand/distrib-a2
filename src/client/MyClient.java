package client;

import common.Constants;
import common.MyStreamSocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class MyClient {
    public static void main(String[] args) {
        String serverHost = "localhost";
        Integer serverPort = Integer.parseInt("12345");

        try {
            MyStreamSocket dataSocket = new MyStreamSocket(
                    serverHost, serverPort);

            // TODO: make menu driven
//            createUser(dataSocket, "abhinav");
//            createUser(dataSocket, "lokesh");
//            createUser(dataSocket, "rawat");

//            String filename = "/home/abnv/Videos/tutorials/Compilers/videos/1-18/1 - 1 - 01-01- Introduction (8m20s).mp4";
//            uploadFile(dataSocket, filename);

//            createFolder(dataSocket, "Apps");

//            String source = "newfile.mp4";
//            String destination = "Apps/newfile.mp4";
//            moveFile(dataSocket, source, destination);

//
//            createGroup(dataSocket, "mango");
//            createGroup(dataSocket, "guava");
//            createGroup(dataSocket, "DS");
//            listGroups(dataSocket);
//            joinGroup(dataSocket, "abhinav", "mango");
//            joinGroup(dataSocket, "lokesh","mango");
//            joinGroup(dataSocket, "rawat","guava");
//            joinGroup(dataSocket, "shubham","mango");
//            joinGroup(dataSocket, "rawat","apple");
//            joinGroup(dataSocket, "rawat","mango");
//            leaveGroup(dataSocket, "abhinav",
//                    "mango");
            listDetail(dataSocket, "mango");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void listDetail(MyStreamSocket dataSocket,
                                   String groupName)
            throws IOException {
        dataSocket.sendMessage(Constants.MessageTypes.LIST_DETAIL);
        dataSocket.sendMessage(groupName);
        HashMap<String, HashSet<String>> filteredUserFiles =
                null;
        try {
            filteredUserFiles = (HashMap<String, HashSet<String>>) dataSocket.receiveObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println(filteredUserFiles);
    }

    private static void leaveGroup(MyStreamSocket dataSocket,
                                   String userName, String groupName)
            throws IOException {
        dataSocket.sendMessage(Constants.MessageTypes.LEAVE_GROUP);
        dataSocket.sendMessage(userName);
        dataSocket.sendMessage(groupName);
        System.out.println(dataSocket.receiveMessage());
    }

    private static void joinGroup(MyStreamSocket dataSocket,
                                  String userName, String groupName)
            throws IOException {
        dataSocket.sendMessage(Constants.MessageTypes.JOIN_GROUP);
        dataSocket.sendMessage(userName);
        dataSocket.sendMessage(groupName);
        System.out.println(dataSocket.receiveMessage());
    }

    private static void listGroups(MyStreamSocket dataSocket)
            throws IOException {
        dataSocket.sendMessage(Constants.MessageTypes.LIST_GROUPS);
        HashSet<String> groups = null;
        try {
            groups = (HashSet<String>) dataSocket.receiveObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println(groups.toString());
    }

    private static void createGroup(MyStreamSocket dataSocket, String groupName) throws IOException {
        dataSocket.sendMessage(Constants.MessageTypes.CREATE_GROUP);
        dataSocket.sendMessage(groupName);
        System.out.println(dataSocket.receiveMessage());
    }

    private static void moveFile(MyStreamSocket dataSocket, String source, String destination)
            throws IOException {
        dataSocket.sendMessage(Constants.MessageTypes.MOVE_FILE);
        dataSocket.sendMessage(source);
        dataSocket.sendMessage(destination);
        System.out.println(dataSocket.receiveMessage());
    }

    private static void createFolder(MyStreamSocket dataSocket, String folderName) throws IOException {
        dataSocket.sendMessage(Constants.MessageTypes.CREATE_FOLDER);
        dataSocket.sendMessage(folderName);

        String response = dataSocket.receiveMessage();
        System.out.println(response);
    }

    private static void uploadFile(MyStreamSocket dataSocket, String filename) throws IOException {
        // TODO: Accept filepath
        dataSocket.sendMessage(Constants.MessageTypes.UPLOAD_FILE);
        dataSocket.sendFile(filename);
    }

    private static void createUser(MyStreamSocket dataSocket, String userName) throws IOException {
        dataSocket.sendMessage(Constants.MessageTypes.CREATE_USER);
        // TODO: Accept username
        dataSocket.sendMessage(userName);

        // response contains whether the command was successful or not
        // so that the client may take further steps
        String response = dataSocket.receiveMessage();
        System.out.println(response);
    }
}
