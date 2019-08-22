package common;

import java.io.*;
import java.net.Socket;

public class MyStreamSocket extends Socket {
    private Socket dataSocket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    ObjectOutputStream objectOutputStream;
    ObjectInputStream objectInputStream;

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

        objectOutputStream = new ObjectOutputStream(dataOutputStream);
        objectInputStream = new ObjectInputStream(dataInputStream);
    }

    public void sendMessage(String message) {
        this.printWriter.println(message);
        this.printWriter.flush();
    }

    public String receiveMessage() throws IOException {
        return this.bufferedReader.readLine();
    }

    public void receiveFile(String newFileName, long numBytes) throws IOException {
        FileOutputStream writeToDisk = new FileOutputStream(newFileName);

        int count;
        byte[] buffer = new byte[8192];
        long bytesRead = 0;
        while ((bytesRead < numBytes) && ((count = dataInputStream.read(buffer)) > 0)) {
            bytesRead += count;
            writeToDisk.write(buffer, 0, count);
            writeToDisk.flush();
        }
        writeToDisk.flush();
        writeToDisk.close();
    }

    public void sendFile(String filePath) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        byte[] buffer = new byte[8192];
        int count;
        while ((count = fis.read(buffer)) > 0) {
            dataOutputStream.write(buffer, 0, count);
            dataOutputStream.flush();
        }
        dataOutputStream.flush();

        fis.close();
//        dataOutputStream.close(); // DONOT close DOS as one client won't be able to send multiple files
    }

    public void sendObject(Object groups)
            throws IOException {
        objectOutputStream.writeObject(groups);
        objectOutputStream.flush();
//        objectOutputStream.close();
    }

    public Object receiveObject()
            throws IOException, ClassNotFoundException {
        Object returnVal = objectInputStream.readObject();
//        objectInputStream.close();
        return returnVal;
    }
}
