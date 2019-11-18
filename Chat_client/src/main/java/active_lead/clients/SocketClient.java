package active_lead.clients;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import active_lead.protocol.*;


public class SocketClient {
    // поле, содержащее сокет-клиента
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private String name;
    private String password;
    private Protocol protocolOut = new Protocol();
    private Protocol protocolIn = new Protocol();
    private ObjectMapper objectMapper = new ObjectMapper();
    private Scanner scanner;

    // начало сессии - получаем ip-сервера и его порт
    public void startConnection(String ip, int port) {
        try {
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            // создаем подключение
            clientSocket = new Socket(ip, port);
            // получили выходной поток
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            // входной поток
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            boolean f = false;
            scanner = new Scanner(System.in);
            do {
                protocolOut.setPayload(new Payload());
                if (isSignUp()) {
                    setNameAndPassword();
                    protocolOut.setHeader("SignUp");
                    System.out.println("CLIENTLOG now prot is " + getValueAsString(protocolOut));
                    f = isUserExists(protocolOut);
                    if (!f) {
                        System.err.println("USER ALREADY EXISTS");
                    } else {
                        protocolOut.setHeader("Login");
                        protocolOut.setPayload(new Payload());
                        saveToken(protocolIn.getPayload().getToken());
                        protocolOut.getPayload().setToken(getToken());
                        protocolOut.getPayload().setTokenExists(true);
//                        System.out.println("CLIENTLOG now prot is " + getValueAsString(protocolOut));
                        out.println(getValueAsString(protocolOut));
                    }
                } else {
                    if (!new File("../token").exists()) {
                        setNameAndPassword();
                    } else {
                        setToken();
                    }
                    protocolOut.setHeader("Login");
//                    System.out.println("CLIENTLOG now prot is " + getValueAsString(protocolOut));
                    f = isUserExists(protocolOut);
                    if (!f) System.err.print("НЕПРАВИЛЬНОЕ ИМЯ ПОЛЬЗОВАТЕЛЯ ИЛИ ПАРОЛЬ\n");
                }
            } while (!f);
            this.name = protocolOut.getPayload().getName();
            this.password = protocolOut.getPayload().getPassword();
            // запустили слушателя сообщений
            new Thread(receiverMessagesTask).start();
            System.out.println("You are connected");
//                clientConnect();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private void setToken() {
        protocolOut.getPayload().setToken(getToken());
        protocolOut.getPayload().setTokenExists(true);
    }

    private String getToken() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File("../token")));
            return br.readLine();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void saveToken(String token) {
        File file = new File("../token");
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(token);
            fw.flush();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void setNameAndPassword() {
        System.out.print("Print name: ");
        protocolOut.getPayload().setName(scanner.nextLine());
        System.out.print("Print password: ");
        protocolOut.getPayload().setPassword(scanner.nextLine());
        System.out.print("Print role (1 if admin, 0 if user): ");
        protocolOut.getPayload().setRole(String.valueOf(scanner.nextInt()));
        protocolOut.getPayload().setTokenExists(false);
    }

    private boolean isSignUp() {
        do {
            System.out.println("SignUp (0) or SignIn (1) ?");
            String a = scanner.nextLine();
            if (a.equals("0")) {
                return true;
            } else if (a.equals("1")) {
                return false;
            }
            System.err.println("I DONT UNDERSTAND");
        } while (true);
    }

    private boolean isUserExists(Protocol protocol) {
        out.println(getValueAsString(protocol));
        try {
            String s = in.readLine();
//            System.out.println("isUserExits " + s);
            protocolIn = getValue(s);
            return protocolIn.getPayload().getCorrect();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private String getValueAsString(Protocol protocol) {
        try {
            return objectMapper.writeValueAsString(protocol);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    private Protocol getValue(String string) {
        try {
            return objectMapper.readValue(string, Protocol.class);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void sendMessage(String text) {
        protocolOut.setPayload(new Payload());
        protocolOut.getPayload().setToken(getToken());
        protocolOut.setHeader("Command");
        switch (text) {
            case "getHistory":
                protocolOut.getPayload().setCommand("history");
                protocolOut.getPayload().setSize(scanner.nextInt());
                protocolOut.getPayload().setPage(scanner.nextInt());
                break;
            case ".":
                protocolOut.setHeader("LogOut");
                break;
            case "showGoods":
                protocolOut.getPayload().setCommand("showGoods");
                break;
            case "buy":
                protocolOut.getPayload().setCommand("buy");
                protocolOut.getPayload().setId(scanner.nextLong());
                break;
            case "addGood":
                protocolOut.getPayload().setCommand("addGood");
                System.out.println("Create good\nSet name");
                protocolOut.getPayload().setName(scanner.nextLine());
                System.out.println("Set price");
                protocolOut.getPayload().setPrice(scanner.nextInt());
                break;
            default:
                protocolOut.setHeader("Message");
                protocolOut.getPayload().setMessage(text);
                break;
        }
        out.println(getValueAsString(protocolOut));
    }

    private Runnable receiverMessagesTask = new Runnable() {
        @Override
        public void run() {
            while (true) {
                String response = "";
                try {
                    response = in.readLine();
                } catch (IOException e) {
                    //  throw new IllegalStateException(e);
                }
                try {
                    if (response != null && !response.equals("")) {
//                        System.out.println(response);
                        protocolIn = objectMapper.readValue(response, Protocol.class);
                        switch (protocolIn.getHeader()) {
                            case "LogOut":
                                System.out.println(protocolIn.getPayload().getName() + " said \"bye\" everyone at " + protocolIn.getPayload().getTime());
                                stopConnection();
                                break;
                            case "Login":
                                System.out.println(protocolIn.getPayload().getName() + " connect to server at " + protocolIn.getPayload().getTime());
                                break;
                            case "Command":
                                String command = protocolIn.getPayload().getCommand();
                                switch (command) {
                                    case "history":
                                        System.out.println("History:");
                                        for (Payload payload : protocolIn.getPayload().getData()) {
                                            System.out.println(payload.getName() + " says \"" + payload.getMessage() + "\" at " + payload.getTime());
                                        }
                                        break;
                                    case "showGoods":
                                        for (Payload payload : protocolIn.getPayload().getData()) {
                                            System.out.println(payload.getId() + " --- " + payload.getName() + " --- " + payload.getPrice());
                                        }
                                        break;
                                    case "message":
                                        System.out.println(protocolIn.getPayload().getMessage());
                                }
                                break;
                            default:
                                System.out.println(protocolIn.getPayload().getName() + " says \"" + protocolIn.getPayload().getMessage() + "\" at " + protocolIn.getPayload().getTime());
                                break;
                        }
                    }
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            }
        }
    };

    public void stopConnection() {
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
//            throw new IllegalStateException(e);
        }
    }
}
