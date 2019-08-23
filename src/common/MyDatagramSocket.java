package common;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class MyDatagramSocket extends DatagramSocket {
    static final int BUFFER_LEN = 1024;

    public MyDatagramSocket() throws SocketException {
        super();
    }

    public MyDatagramSocket(int port) throws SocketException {
        super(port);
    }

    public void sendMessage(InetAddress receiverHost, int receiverPort, String message) throws IOException {
        byte[] sendBuffer = message.getBytes();
        DatagramPacket datagram = new DatagramPacket(sendBuffer, sendBuffer.length,
                receiverHost, receiverPort);
        this.send(datagram);
    }

    public void sendFile(InetAddress receiverHost, int receiverPort, String filePath)
            throws IOException {
        FileInputStream readFromDisk = new FileInputStream(filePath);

        // FIXME: replace file.length() to get exact size
//        long fileSize = new File(filePath).length();
        int count;
        byte[] buffer = new byte[BUFFER_LEN];
        while ((count = readFromDisk.read(buffer)) > 0) {
            DatagramPacket datagram =
                    new DatagramPacket(buffer,
                            count,
                            receiverHost,
                            receiverPort);
            this.send(datagram);
        }
    }

    public String receiveMessage() throws IOException {
        byte[] receiveBuffer = new byte[BUFFER_LEN];
        DatagramPacket datagram = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        this.receive(datagram);
        return new String(receiveBuffer);
    }

    public void receiveFile(String newFileName, long numBytes)
            throws IOException {
        FileOutputStream writeToDisk = new FileOutputStream(newFileName);

        long bytesRead = 0;
        while (bytesRead < numBytes) {
            byte[] buffer = new byte[BUFFER_LEN];
            DatagramPacket datagram = new DatagramPacket
                    (buffer, Math.min(buffer.length, (int) (numBytes - bytesRead)));
            this.receive(datagram);

            bytesRead += buffer.length;
            writeToDisk.write(buffer, 0, buffer.length);
            writeToDisk.flush();
        }
        writeToDisk.flush();
        writeToDisk.close();
    }
}
