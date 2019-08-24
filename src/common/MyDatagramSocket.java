package common;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class MyDatagramSocket extends DatagramSocket {
    static final int BUFFER_LEN = 9999999;

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
        File file = new File(filePath);
        byte[] buffer = new byte[(int) file.length()];
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
        bufferedInputStream.read(buffer);
        DatagramPacket datagram = new DatagramPacket(buffer,
                buffer.length,
                receiverHost,
                receiverPort);
        this.send(datagram);
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
        byte[] buffer = new byte[(int) numBytes];

        DatagramPacket datagram = new DatagramPacket
                (buffer, buffer.length);
        this.receive(datagram);

        writeToDisk.write(datagram.getData());
        writeToDisk.flush();
        writeToDisk.close();
    }
}
