package client;

import common.Constants;

import java.io.*;
import java.net.Socket;

public class MyClient {
    public static void main(String[] args) {
        String serverHost = "localhost";
        Integer serverPort = Integer.parseInt("12345");

        try {
            Socket dataSocket = new Socket(serverHost, serverPort);
            OutputStream outputStream = dataSocket.getOutputStream();
            PrintWriter output = new PrintWriter(new OutputStreamWriter(outputStream));
            InputStream inputStream = dataSocket.getInputStream();
            BufferedReader input = new BufferedReader(new InputStreamReader(inputStream));

            // TODO: make menu driven
//            createUser(input, output);
            uploadFile(dataSocket, output);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void uploadFile(Socket dataSocket, PrintWriter output) throws IOException {
        output.println(Constants.MessageTypes.UPLOAD_FILE);
        output.flush();

        FileInputStream fis = new FileInputStream("/home/abnv/Videos/tutorials/CCNA/01 - CCNA R and S Exam Course - Introduction.mp4");
        DataOutputStream dos = new DataOutputStream(dataSocket.getOutputStream());
        byte[] buffer = new byte[8192];
        int count;
        while ((count = fis.read(buffer)) > 0) {
            System.out.println("writing buffer to stream");
            dos.write(buffer, 0, count);
            dos.flush();
        }
        dos.close();
    }

    private static void createUser(BufferedReader input, PrintWriter output) throws IOException {
        output.println(Constants.MessageTypes.CREATE_USER);
        output.println("abhinav");
        output.flush();

        // response contains whether the command was successful or not
        // so that the client may take further steps
        String response = input.readLine();
        System.out.println(response);
    }
}
