package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class ReadThread implements Runnable {
    static final int MAX_LEN = 30;
    private InetAddress groupAddress;
    private int port;
    private MulticastSocket multicastSocket = null;

    public ReadThread(InetAddress groupAddress, int port) {
        this.groupAddress = groupAddress;
        this.port = port;
        try {
            multicastSocket = new MulticastSocket(port);
            // Receiver must join group
            multicastSocket.joinGroup(groupAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void leaveGroup() {
        try {
            multicastSocket.leaveGroup(groupAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                byte[] data = new byte[MAX_LEN];
                DatagramPacket packet = new DatagramPacket(data, data.length, groupAddress, port);
                multicastSocket.receive(packet);
                if (Thread.currentThread().isInterrupted())
                    break;
                String receivedMsg = new String(packet.getData());
                System.out.println("Received group message: " + receivedMsg);
            }
            leaveGroup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
