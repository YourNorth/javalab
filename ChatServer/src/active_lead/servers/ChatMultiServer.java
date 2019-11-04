package die.mass.servers;

import die.mass.protocol.Message;
import die.mass.protocol.Payload;
import die.mass.protocol.Protocol;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import die.mass.DBLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ChatMultiServer {

    // список клиентов
    private List<ClientHandler> clients;
    private DBLoader dbLoader;
    private ObjectMapper objectMapper;
    private Protocol protocolOut = new Protocol();
    private Protocol protocolIn = new Protocol();
    private PasswordEncoder encoder;
    private GetTokenServiceImpl getTokenService = new GetTokenServiceImpl("qwerty007");

    public ChatMultiServer() {
        // Список для работы с многопоточностью
        clients = new CopyOnWriteArrayList<>();
    }

    public void start(int port, String pathToProperties) {
        ServerSocket serverSocket;
        dbLoader = DBLoader.create(pathToProperties);
        objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
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

    private class ClientHandler extends Thread {
        // связь с одним клиентом
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;
        private String name;
        private String password;

        private ClientHandler(Socket socket) {
            encoder = new BCryptPasswordEncoder();

            this.clientSocket = socket;
            // добавляем текущее подключение в список
            clients.add(this);
            try {
                in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                boolean f;
                do {
                    parseString();
                    if (protocolIn.getHeader().equals("SignUp")) {
                        f = checkToNotExistsWithLogin();
                        if (f) {
                            saveUser(name, password);
                            checkToExistsWithLogin();
                        }
                    } else {
                        if(protocolIn.getPayload().getTokenExists()) {
                            f = checkToExistsWithToken();
                        } else f = checkToExistsWithLogin();
                    }
                } while (!f);
                System.out.println("New client " + name);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }


        private void parseString() {
            try {
                String string = in.readLine();
//                System.out.println("input string is " + string);
                protocolIn = objectMapper.readValue(string, Protocol.class);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        private boolean checkToNotExistsWithLogin() {
            name = protocolIn.getPayload().getName();
            password = protocolIn.getPayload().getPassword();
            boolean f = !accountExists(name, password);
            protocolOut.setHeader("Command");
            protocolOut.setPayload(new Payload());
            protocolOut.getPayload().setCommand("Login");
            protocolOut.getPayload().setCorrect(f);
            if (f) protocolOut.getPayload().setToken(getTokenService.getToken(name, password, "user"));
            try {
//                System.out.println("SERVERLOG: checkToNotExists() " + objectMapper.writeValueAsString(protocolOut));
                out.println(objectMapper.writeValueAsString(protocolOut));
            } catch (JsonProcessingException e) {
                throw new IllegalStateException(e);
            }
            return f;
        }

        private boolean checkToExistsWithLogin() {
            name = protocolIn.getPayload().getName();
            password = protocolIn.getPayload().getPassword();
            return checkToExists();
        }

        private boolean checkToExistsWithToken() {
            String token = protocolIn.getPayload().getToken();
            name = getTokenService.getData(token, "name");
            password = getTokenService.getData(token, "password");
            return checkToExists();
        }

        private boolean checkToExists() {
            boolean f = accountExists(name, password);
            protocolOut.setPayload(new Payload());
            protocolOut.setHeader("Command");
            protocolOut.getPayload().setCommand("Login");
            protocolOut.getPayload().setCorrect(f);

            try {
//                System.out.println("SERVERLOG: checkToExists() " + objectMapper.writeValueAsString(protocolOut));
                out.println(objectMapper.writeValueAsString(protocolOut));
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
            return f;
        }

        private boolean accountExists(String name, String password) {
            String p = dbLoader.findUser(name);
            return encoder.matches(password, p);
        }


        private boolean saveUser(String name, String password) {
            return dbLoader.saveUser(name, encoder.encode(password));
        }

        public void run() {
            try {
                // получем входной поток для конкретного клиента
                in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
                String inputLine;
                LocalTime localTime;
                while ((inputLine = in.readLine()) != null) {
                    protocolIn = objectMapper.readValue(inputLine, Protocol.class);
                    String header = protocolIn.getHeader();
                    if (header.equals("LogOut")) {
                        // бегаем по всем клиентам и обовещаем их о событии
                        localTime = LocalTime.now();
                        protocolOut.setHeader("LogOut");
                        protocolOut.setPayload(new Payload());
                        protocolOut.getPayload().setName(this.name);
                        protocolOut.getPayload().setTime(localTime.toString());
                        for (ClientHandler client : clients) {
                            PrintWriter out = new PrintWriter(client.clientSocket.getOutputStream(), true);
                            out.println(objectMapper.writeValueAsString(protocolOut));
                        }
                        clients.remove(this);
                        break;
                    } else if (header.equals("Command")) {
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                        Integer page = protocolIn.getPayload().getPage();
                        Integer size = protocolIn.getPayload().getSize();
                        protocolOut.setHeader("Command");
                        protocolOut.setPayload(new Payload());
                        protocolOut.getPayload().setData(new ArrayList(dbLoader.getHistory(size, page).stream().map(i -> new Message(i.getName(), i.getContent(), i.getTime())).collect(Collectors.toList())));
                        out.println(objectMapper.writeValueAsString(protocolOut));
                    } else {
                        localTime = LocalTime.now();
                        String message = protocolIn.getPayload().getMessage();
                        protocolOut.setPayload(new Payload());
                        protocolOut.getPayload().setName(this.name);
                        protocolOut.getPayload().setTime(localTime.toString());
                        if (message == null) {
                            protocolOut.setHeader("Login");
                        } else {
                            dbLoader.saveMessage(message, name, localTime.toString());
                            protocolOut.setHeader("Message");
                            protocolOut.getPayload().setMessage(protocolIn.getPayload().getMessage());
                        }
                        for (ClientHandler client : clients) {
                            PrintWriter out = new PrintWriter(client.clientSocket.getOutputStream(), true);
                            out.println(objectMapper.writeValueAsString(protocolOut));
                        }
                    }
                }
                in.close();
                clientSocket.close();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
