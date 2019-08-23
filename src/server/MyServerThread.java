package server;

import common.Constants;
import common.MyDatagramSocket;
import common.MyStreamSocket;

import java.io.IOException;

public class MyServerThread implements Runnable {
    private MyStreamSocket dataSocket;
    private MyDatagramSocket myDatagramSocket;

    MyServerThread(MyStreamSocket myStreamSocket, MyDatagramSocket myDatagramSocket) {
        this.dataSocket = myStreamSocket;
        this.myDatagramSocket = myDatagramSocket;
    }

    @Override
    public void run() {
        boolean done = false;
        while (!done) {
            // Message is received in the form of multiple lines
            // First line specifies message type
            String msgType = null;
            try {
                msgType = dataSocket.receiveMessage();

                System.out.println("msgType: " + msgType);
                if (Constants.MessageTypes.QUIT.equalsIgnoreCase(msgType)) {
                    dataSocket.close();
                    done = true;
                } else if (Constants.MessageTypes.CREATE_USER.equals(msgType)) {
                    MyServer.createUser(dataSocket);
                } else if (Constants.MessageTypes.UPLOAD_FILE.equals(msgType)) {
                    MyServer.uploadFile(dataSocket);
                } else if (Constants.MessageTypes.CREATE_FOLDER.equals(msgType)) {
                    MyServer.createFolder(dataSocket);
                } else if (Constants.MessageTypes.MOVE_FILE.equals(msgType)) {
                    MyServer.moveFile(dataSocket);
                } else if (Constants.MessageTypes.CREATE_GROUP.equals(msgType)) {
                    MyServer.createGroup(dataSocket);
                } else if (Constants.MessageTypes.LIST_GROUPS.equals(msgType)) {
                    MyServer.listGroups(dataSocket);
                } else if (Constants.MessageTypes.JOIN_GROUP.equals(msgType)) {
                    MyServer.joinGroup(dataSocket);
                } else if (Constants.MessageTypes.LEAVE_GROUP.equals(msgType)) {
                    MyServer.leaveGroup(dataSocket);
                } else if (Constants.MessageTypes.LIST_DETAIL.equals(msgType)) {
                    MyServer.listDetail(dataSocket);
                } else if (Constants.MessageTypes.GET_FILE.equals(msgType)) {
                    MyServer.getFile(dataSocket);
                } else if (Constants.MessageTypes.UPLOAD_UDP.equals(msgType)) {
                    MyServer.uploadUdp(dataSocket, myDatagramSocket);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}