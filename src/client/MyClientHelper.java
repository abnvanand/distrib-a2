package client;

import common.Constants;
import common.MyDatagramSocket;
import common.MyStreamSocket;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;

public class MyClientHelper {
    private String myUserName;
    private MyStreamSocket dataSocket;
    private String serverHost;
    private String serverPort;
    private String serverUDPPort;
    // <GroupName, Thread>
    private HashMap<String, Thread> groupChatThreads = new HashMap<>();

    MyClientHelper(String serverHost, String serverPort, String serverUDPPort) throws IOException {
        this.dataSocket = new MyStreamSocket(
                serverHost, Integer.parseInt(serverPort));
        this.serverHost = serverHost;
        this.serverUDPPort = serverUDPPort;
    }

    public String getMyUserName() {
        return myUserName;
    }

    public void getFile(String groupName, String userName, String filePath)
            throws IOException {
        this.dataSocket.sendMessage(Constants.MessageTypes.GET_FILE);
        this.dataSocket.sendMessage(groupName);
        this.dataSocket.sendMessage(userName);
        this.dataSocket.sendMessage(filePath);
        // receives whether file exists or not
        String response = this.dataSocket.receiveMessage();
        if (response.equalsIgnoreCase("error")) {
            System.out.println("Error: invalid file referred");
            return;
        }
        // response either contains "error" or the filesize
        String fileSize = response;
        System.out.println("Received file size: " + fileSize);

        System.out.println(String.format("Receiving file: %s/%s", userName, filePath));
        File file = new File(filePath);
        this.dataSocket.receiveFile(file.getName(), Long.parseLong(fileSize));
    }

    public void listDetail(String groupName)
            throws IOException {
        this.dataSocket.sendMessage(Constants.MessageTypes.LIST_DETAIL);
        this.dataSocket.sendMessage(groupName);
//        HashMap<String, HashSet<String>> filteredUserFiles = null;
//        try {
//            filteredUserFiles = (HashMap<String, HashSet<String>>) this.dataSocket.receiveObject();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        System.out.println(filteredUserFiles);
        System.out.println(this.dataSocket.receiveMessage());
    }

    public void leaveGroup(String userName, String groupName)
            throws IOException {
        this.dataSocket.sendMessage(Constants.MessageTypes.LEAVE_GROUP);
        this.dataSocket.sendMessage(userName);
        this.dataSocket.sendMessage(groupName);
        String response = this.dataSocket.receiveMessage();
        if (response.equalsIgnoreCase("success")) {
            System.out.println("here1");
            String groupPort = this.dataSocket.receiveMessage();
            System.out.println("here2" + groupPort);
            // TODO: finish thread of corresponding group
            Thread thread = groupChatThreads.get(groupName);
            System.out.println("here3");
            thread.interrupt();
            System.out.println("here4");
            groupChatThreads.remove(groupName);

        } else {
            System.out.println("Here else");
        }
    }

    public void joinGroup(String userName, String groupName)
            throws IOException {
        this.dataSocket.sendMessage(Constants.MessageTypes.JOIN_GROUP);
        this.dataSocket.sendMessage(userName);
        this.dataSocket.sendMessage(groupName);
        String response = this.dataSocket.receiveMessage();
        if (!response.equalsIgnoreCase("success")) {
            System.out.println(response);
            return;
        }
        // else get group's port number
        String multicastPort = this.dataSocket.receiveMessage();

        // Read msgs on separate thread
        // TODO: Find a way to finish this thread when user leaves the group
        Thread theThread = new Thread(new ReadThread(
                InetAddress.getByName(Constants.MULTICAST_ADDRESS),
                Integer.parseInt(multicastPort)));
        groupChatThreads.put(groupName, theThread);
        theThread.start();
    }

    public void listGroups()
            throws IOException {
        this.dataSocket.sendMessage(Constants.MessageTypes.LIST_GROUPS);
//        HashSet<String> groups = null;
//        try {
//            groups = (HashSet<String>) this.dataSocket.receiveObject();
//            System.out.println(groups.toString());
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
        System.out.println(this.dataSocket.receiveMessage());
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

    public void uploadUdp(String userName, String fileName, String filePath, long fileSize) throws IOException {
        this.dataSocket.sendMessage(Constants.MessageTypes.UPLOAD_UDP);
        this.dataSocket.sendMessage(userName);
        this.dataSocket.sendMessage(fileName);
        this.dataSocket.sendMessage(String.valueOf(fileSize));
        System.out.println("Sending file: " + filePath);
        // No need to bind client to a port
        MyDatagramSocket myDatagramSocket = new MyDatagramSocket();
        // TODO:
        myDatagramSocket.sendFile(InetAddress.getByName(this.serverHost), Integer.parseInt(serverUDPPort), filePath);
//        myDatagramSocket.sendMessage(InetAddress.getByName(this.serverHost), Integer.parseInt(serverUDPPort), filePath);
        myDatagramSocket.close();
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

    public void shareMsg(String myUserName, String groupName, String message) throws IOException {
        this.dataSocket.sendMessage(Constants.MessageTypes.SHARE_MSG);
        this.dataSocket.sendMessage(myUserName);
        this.dataSocket.sendMessage(groupName);
        this.dataSocket.sendMessage(message);   // client sends msg to server over TCP
        // server multicasts that msg to the group
    }
}
