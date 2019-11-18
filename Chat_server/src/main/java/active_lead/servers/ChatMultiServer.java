package active_lead.servers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatMultiServer {

    // список клиентов
    private List<ClientHandler> clients;
    private ServerContext serverContext;
    private MessageResolver messageResolver;

    public ChatMultiServer() {
        // Список для работы с многопоточностью
        clients = new CopyOnWriteArrayList<>();
    }

    public void start(int port, String pathToProperties) {
        ServerSocket serverSocket;
        serverContext = new ServerContext(pathToProperties);
        messageResolver = new MessageResolver(serverContext, clients);

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        // запускаем бесконечный цикл
        while (true) {
            try {
                // запускаем обработчик сообщений для каждого подключаемого клиента
                new ClientHandler(serverSocket.accept()).start();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    public class ClientHandler extends Thread {
        // связь с одним клиентом
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;
        private MessageResolver clientMessageResolver;
        private String name;

        private ClientHandler(Socket socket) {
            this.clientMessageResolver = messageResolver;
            this.clientSocket = socket;
            // добавляем текущее подключение в список
            clients.add(this);
            try {
                in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                do {
                    name = clientMessageResolver.successfullyEntering(in.readLine(), out);
                } while (name == null);
                System.out.println("New client " + name);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        public PrintWriter getOut() {
            return out;
        }

        public String getClientName() {
            return name;
        }

        public void run() {
            try {
                // получем входной поток для конкретного клиента
                in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    String answer = messageResolver.listen(inputLine);
                    if(answer.equals("delete")) clients.remove(this);
                }
                in.close();
                clientSocket.close();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
