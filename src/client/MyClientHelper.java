package client;

import common.Constants;
import common.MyStreamSocket;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class MyClientHelper {
    private String myUserName;
    private MyStreamSocket dataSocket;

    MyClientHelper(String serverHost, String serverPort) throws IOException {
        this.dataSocket = new MyStreamSocket(
                serverHost, Integer.parseInt(serverPort));
    }

    public String getMyUserName() {
        return myUserName;
    }

    public void getFile(String groupName, String userName, String filePath)
            throws IOException {
        this.dataSocket.sendMessage(groupName);
        this.dataSocket.sendMessage(userName);
        this.dataSocket.sendMessage(filePath);
        // receives whether file exists or not
        String response = this.dataSocket.receiveMessage();
        if (response.equalsIgnoreCase("error")) {
            System.out.println("Error: invalid file referred");
            return;
        }
        System.out.println(String.format("Receiving file: %s/%s", userName, filePath));
        File file = new File(filePath);
        this.dataSocket.receiveFile(file.getName(), file.length());
    }

    public void listDetail(String groupName)
            throws IOException {
        this.dataSocket.sendMessage(Constants.MessageTypes.LIST_DETAIL);
        this.dataSocket.sendMessage(groupName);
        HashMap<String, HashSet<String>> filteredUserFiles =
                null;
        try {
            filteredUserFiles = (HashMap<String, HashSet<String>>) this.dataSocket.receiveObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println(filteredUserFiles);
    }

    public void leaveGroup(String userName, String groupName)
            throws IOException {
        this.dataSocket.sendMessage(Constants.MessageTypes.LEAVE_GROUP);
        this.dataSocket.sendMessage(userName);
        this.dataSocket.sendMessage(groupName);
        System.out.println(this.dataSocket.receiveMessage());
    }

    public void joinGroup(String userName, String groupName)
            throws IOException {
        this.dataSocket.sendMessage(Constants.MessageTypes.JOIN_GROUP);
        this.dataSocket.sendMessage(userName);
        this.dataSocket.sendMessage(groupName);
        System.out.println(this.dataSocket.receiveMessage());
    }

    public void listGroups()
            throws IOException {
        this.dataSocket.sendMessage(Constants.MessageTypes.LIST_GROUPS);
        HashSet<String> groups = null;
        try {
            groups = (HashSet<String>) this.dataSocket.receiveObject();
            System.out.println(groups.toString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void createGroup(String groupName) throws IOException {
        this.dataSocket.sendMessage(Constants.MessageTypes.CREATE_GROUP);
        this.dataSocket.sendMessage(groupName);
        System.out.println(this.dataSocket.receiveMessage());
    }

    public void moveFile(String userName, String source, String destination)
            throws IOException {
        this.dataSocket.sendMessage(Constants.MessageTypes.MOVE_FILE);
        this.dataSocket.sendMessage(userName);
        this.dataSocket.sendMessage(source);
        this.dataSocket.sendMessage(destination);
        System.out.println(this.dataSocket.receiveMessage());
    }

    public void createFolder(String userName, String folderName)
            throws IOException {
        this.dataSocket.sendMessage(Constants.MessageTypes.CREATE_FOLDER);
        this.dataSocket.sendMessage(userName);
        this.dataSocket.sendMessage(folderName);

        String response = this.dataSocket.receiveMessage();
        System.out.println(response);
    }

    public void uploadFile(String userName, String fileName, String filePath, long fileSize)
            throws IOException {
        this.dataSocket.sendMessage(Constants.MessageTypes.UPLOAD_FILE);
        this.dataSocket.sendMessage(userName);
        this.dataSocket.sendMessage(fileName);
        this.dataSocket.sendMessage(String.valueOf(fileSize));
        System.out.println("Sending file: " + filePath);
        this.dataSocket.sendFile(filePath);
    }

    public void createUser(String userName)
            throws IOException {
        this.dataSocket.sendMessage(Constants.MessageTypes.CREATE_USER);
        this.dataSocket.sendMessage(userName);

        // response contains whether the command was successful or not
        // so that the client may take further steps
        String response = this.dataSocket.receiveMessage();
        System.out.println(response);
        if (!"Error".equalsIgnoreCase(response))
            this.myUserName = userName;
    }

    public void done() throws IOException {
        this.dataSocket.sendMessage(Constants.MessageTypes.QUIT);
        this.dataSocket.close();
    }
}
