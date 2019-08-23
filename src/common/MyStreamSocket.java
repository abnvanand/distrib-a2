package common;

import java.io.*;
import java.net.Socket;

public class MyStreamSocket extends Socket {
    private Socket dataSocket;
//    private BufferedReader bufferedReader;
//    private PrintWriter printWriter;
//    private InputStreamReader inputStreamReader;
//    private OutputStreamWriter outputStreamWriter;

    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
//    private ObjectOutputStream objectOutputStream;
//    private ObjectInputStream objectInputStream;

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

//        inputStreamReader = new InputStreamReader(inStream);
//        outputStreamWriter = new OutputStreamWriter(outStream);

//        this.bufferedReader = new BufferedReader(inputStreamReader);
//        this.printWriter = new PrintWriter(outputStreamWriter);
//
        /*
        There should be no problem creating two readers for the same inputstream.
        The problem is knowing when (and how much) to read which reader.
        They will both consume (and advance) the underlying stream when you read from them, since you have mixed types of data.
          You could just read the stream as bytes and then convert the bytes explicitly in your code (new String(bytes, "UTF-8") etc).
          Or you could split your communication onto two different sockets. – pap(SO)
         */
        this.dataInputStream = new DataInputStream(inStream);
        this.dataOutputStream = new DataOutputStream(outStream);
//
//        objectOutputStream = new ObjectOutputStream(dataOutputStream);
//        objectInputStream = new ObjectInputStream(dataInputStream);
    }

    public synchronized void sendMessage(String message) throws IOException {
        this.dataOutputStream.writeBytes(message);
        this.dataOutputStream.write(0x0A);  // write \n character at the end for comm msgs
        this.dataOutputStream.flush();
//        this.printWriter.println(message);
//        this.printWriter.flush();
    }

    public synchronized String receiveMessage() throws IOException {
        return readLine(this.dataInputStream);
        //        return this.bufferedReader.readLine();
    }

    public synchronized void receiveFile(String newFileName, long numBytes) throws IOException {
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

    public synchronized void sendFile(String filePath) throws IOException {
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

    public synchronized void sendObject(Object groups)
            throws IOException {
//        objectOutputStream.writeObject(groups);
//        objectOutputStream.flush();
//        objectOutputStream.close();
    }

    public synchronized Object receiveObject()
            throws IOException, ClassNotFoundException {
//        Object returnVal = objectInputStream.readObject();
//        objectInputStream.close();
//        return returnVal;
        return null;
    }

    /**
     * Reads UTF-8 character data; lines are terminated with '\n'
     */
    private static String readLine(InputStream in) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        while (true) {
            int b = in.read();
            if (b < 0) {
                throw new IOException("Data truncated");
            } else if (b == 0x0A) {
                // If b is a new line character
                // we have receive a comm message
                break;
            }
            buffer.write(b);
        }
        return new String(buffer.toByteArray(), "UTF-8");
    }
}
