package client;

import common.Constants;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import static common.Constants.UPLOAD_PATH;

public class MyClient {
    public static void main(String[] args) {
        File uploadFolder = new File(UPLOAD_PATH);
        uploadFolder.mkdir();

        Scanner in = new Scanner(System.in);

        System.out.println("Enter server hostname (default localhost)");
        String serverHost = in.nextLine();
        if (serverHost.trim().isEmpty()) {
            serverHost = "localhost";
        }

        System.out.println("Enter server port (default 12345)");
        String serverPort = in.nextLine();
        if (serverPort.trim().isEmpty()) {
            serverPort = "12345";
        }

        System.out.println("Enter server UDP port (default 12346)");
        String serverUDPPort = in.nextLine();
        if (serverUDPPort.trim().isEmpty()) {
            serverUDPPort = "12346";
        }

        try {
            MyClientHelper helper = new MyClientHelper(serverHost, serverPort, serverUDPPort);
            while (true) {

                System.out.println("Enter command in a single line `quit` to end the session");
                String command = in.nextLine();
                String[] split = command.split(" ");
                System.out.println("command: " + command);
                System.out.println("Splits: " + split);
                String msgType = split[0];
                try {

                    if (Constants.MessageTypes.QUIT.equalsIgnoreCase(split[0])) {
                        helper.done();
                        break;  // break from loop

                    } else if (Constants.MessageTypes.CREATE_USER.equals(msgType)) {
                        System.out.println("msgType: " + msgType);

                        helper.createUser(split[1]);

                    } else if (Constants.MessageTypes.UPLOAD_FILE.equals(msgType)) {
                        File f = new File(split[1]);

                        helper.uploadFile(helper.getMyUserName(), f.getName(), split[1], f.length());

                    } else if (Constants.MessageTypes.CREATE_FOLDER.equals(msgType)) {
                        helper.createFolder(helper.getMyUserName(), split[1]);

                    } else if (Constants.MessageTypes.MOVE_FILE.equals(msgType)) {
                        helper.moveFile(helper.getMyUserName(), split[1], split[2]);

                    } else if (Constants.MessageTypes.CREATE_GROUP.equals(msgType)) {
                        helper.createGroup(split[1]);

                    } else if (Constants.MessageTypes.LIST_GROUPS.equals(msgType)) {
                        helper.listGroups();

                    } else if (Constants.MessageTypes.JOIN_GROUP.equals(msgType)) {
                        helper.joinGroup(helper.getMyUserName(), split[1]);

                    } else if (Constants.MessageTypes.LEAVE_GROUP.equals(msgType)) {
                        helper.leaveGroup(helper.getMyUserName(), split[1]);

                    } else if (Constants.MessageTypes.LIST_DETAIL.equals(msgType)) {
                        helper.listDetail(split[1]);

                    } else if (Constants.MessageTypes.GET_FILE.equals(msgType)) {
                        String[] pathSplits = split[1].split("/");
                        String groupName = pathSplits[0];
                        String userName = pathSplits[1];
                        String filePath = pathSplits[2];
                        helper.getFile(groupName, userName, filePath);
                    } else if (Constants.MessageTypes.UPLOAD_UDP.equals(msgType)) {
                        File f = new File(split[1]);

                        helper.uploadUdp(helper.getMyUserName(), f.getName(), split[1], f.length());
                    }
                } catch (IOException e) {
                    System.out.println(e.getLocalizedMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
