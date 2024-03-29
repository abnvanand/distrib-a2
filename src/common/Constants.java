package common;

public class Constants {
    public static class MessageTypes {
        public static final String CREATE_USER = "create_user";

        public static final String UPLOAD_FILE = "upload";
        public static final String CREATE_FOLDER = "create_folder";
        public static final String MOVE_FILE = "move_file";

        public static final String CREATE_GROUP = "create_group";
        public static final String LIST_GROUPS = "list_groups";
        public static final String JOIN_GROUP = "join_group";
        public static final String LEAVE_GROUP = "leave_group";
        public static final String LIST_DETAIL = "list_detail";
        public static final String GET_FILE = "get_file";
        public static final String UPLOAD_UDP = "upload_udp";
        public static final String SHARE_MSG = "share_msg";

        public static final String QUIT = "quit";
    }

    public static final String UPLOAD_PATH = "uploads";
    public static final String MULTICAST_ADDRESS = "239.1.2.13"; // TODO: Set by scanner.in
    public static final int MULTICAST_TTL = 32; // TODO: find best choice
}
