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
            createUser(input, output);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
