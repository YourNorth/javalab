package active_lead.programs.clients;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import active_lead.clients.SocketClient;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

@Parameters(separators = "=")
public class ProgramClientChatStart {

    @Parameter(names = {"--server-ip"})
    private static String ip = "127.0.0.1";
    @Parameter(names = {"--server-port"})
    private static int port = 6666;

    public static void main(String[] args) {
        ProgramClientChatStart main = new ProgramClientChatStart();
        JCommander.newBuilder()
                .addObject(main)
                .build()
                .parse(args);
        main.run();
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        SocketClient client = new SocketClient();
        client.startConnection(ip, port);
        while (true) {
            String message = scanner.nextLine();
            client.sendMessage(message);
        }
    }
}
