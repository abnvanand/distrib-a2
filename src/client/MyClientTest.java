package client;

import java.io.File;
import java.io.IOException;

import static common.Constants.UPLOAD_PATH;

class MyClientTest {

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        File uploadFolder = new File(UPLOAD_PATH);
        uploadFolder.mkdir();
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
//        File uploadFolder = new File(UPLOAD_PATH);
//        uploadFolder.delete();
    }


    @org.junit.jupiter.api.Test
    void main() throws IOException {
        MyClientHelper helper = new MyClientHelper("localhost", "12345", "12346");

        helper.createUser("abhi");
        File file = new File("/home/abnv/Desktop/video.mp4");
        helper.uploadFile("abhi","test.mp4", "/home/abnv/Desktop/video.mp4", file.length());
        helper.createGroup("java");
        helper.joinGroup("abhi", "java");

        helper.createFolder("abhi", "Apps");
        helper.createFolder("abhi", "to_move");
        helper.moveFile("abhi", "to_move", "Apps/to_move");
        helper.createGroup("python");
        helper.listGroups();
        helper.joinGroup("abhi", "python");
//        helper.createUser("rawat");
        helper.listDetail("java");
        helper.listDetail("python");


        helper.createUser("loki");
        helper.joinGroup("loki", "java");
        helper.joinGroup("loki", "python");
        helper.leaveGroup("loki", "python");


        helper.getFile("java", "abhi","video.mp4");
    }
}