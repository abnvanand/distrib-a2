package common;

import java.io.*;
import java.net.Socket;

public class MyStreamSocket extends Socket {
    private Socket dataSocket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public MyStreamSocket(String acceptorHost, int acceptorPort) throws IOException {
        this.dataSocket = new Socket(acceptorHost, acceptorPort);
        setStreams();
    }

    public MyStreamSocket(Socket socket) throws IOException {
        this.dataSocket = socket;
        setStreams();
    }

    private void setStreams() throws IOException {
        InputStream inStream = dataSocket.getInputStream();
        OutputStream outStream = dataSocket.getOutputStream();

        this.bufferedReader = new BufferedReader(new InputStreamReader(inStream));
        this.printWriter = new PrintWriter(new OutputStreamWriter(outStream));

        this.dataInputStream = new DataInputStream(inStream);
        this.dataOutputStream = new DataOutputStream(outStream);
    }

    public void sendMessage(String message) {
        this.printWriter.println(message);
        this.printWriter.flush();
    }

    public String receiveMessage() throws IOException {
        return this.bufferedReader.readLine();
    }

    public void receiveFile(String fileName) throws IOException {
        FileOutputStream writeToDisk = new FileOutputStream(fileName);

        int count;
        byte[] buffer = new byte[8192];
        while ((count = this.dataInputStream.read(buffer)) > 0) {
            writeToDisk.write(buffer, 0, count);
            writeToDisk.flush();
        }
        writeToDisk.close();
    }

    public void sendFile(String filename) throws IOException {
        FileInputStream fis = new FileInputStream(filename);
        byte[] buffer = new byte[8192];
        int count;
        while ((count = fis.read(buffer)) > 0) {
            System.out.println("writing buffer to stream");
            dataOutputStream.write(buffer, 0, count);
            dataOutputStream.flush();
        }
        fis.close();
//        dataOutputStream.close(); // DONOT close DOS as one client won't be able to send multiple files
    }
}
