package active_lead.programs.servers;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import active_lead.servers.ChatMultiServer;

@Parameters(separators = "=")
public class ProgramChatMultiServer {

    @Parameter(names = {"--db-properties"})
    private String pathToProperties = "/media/diemass/DATA/Projects/Java/Education/JavaLab/Chatserver/src/main/resources/db.properties";
    //    private static String pathToProperties = "D:\\Projects\\Java\\Education\\JavaLab\\Chat\\db.properties";
    @Parameter(names = {"--port"})
    private static int port = 6666;

    public static void main(String[] args) {
        ProgramChatMultiServer main = new ProgramChatMultiServer();
        JCommander.newBuilder()
                .addObject(main)
                .build()
                .parse(args);
        main.run();
    }

    public void run() {
        System.out.println(port);
        ChatMultiServer server = new ChatMultiServer();
        server.start(port, pathToProperties);
    }
}