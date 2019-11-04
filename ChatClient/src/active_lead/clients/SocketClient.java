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
//                    System.out.println("CLIENTLOG now prot is " + getValueAsString(protocolOut));
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
                    if(!new File("../token").exists()) {
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
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }


    public void sendMessage(String text) {
        protocolOut.setPayload(new Payload());
        if (text.equals("getHistory")) {
            protocolOut.setHeader("Command");
            protocolOut.getPayload().setSize(scanner.nextInt());
            protocolOut.getPayload().setPage(scanner.nextInt());
        } else if (text.equals(".")){
            protocolOut.setHeader("LogOut");
        }
            else  {
            protocolOut.setHeader("Message");
            protocolOut.getPayload().setMessage(text);
        }
        out.println(getValueAsString(protocolOut));
    }

    private Runnable receiverMessagesTask = new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    String response = in.readLine();
                    if (response != null) {
//                        System.out.println(response);
                        protocolIn = objectMapper.readValue(response, Protocol.class);
                        if(protocolIn.getHeader().equals("LogOut")) {
                            System.out.println(protocolIn.getPayload().getName() + " said \"bye\" everyone at " + protocolIn.getPayload().getTime());
                        } else if(protocolIn.getHeader().equals("Login")) {
                            System.out.println(protocolIn.getPayload().getName() + " connect to server at " + protocolIn.getPayload().getTime());
                        } else if(protocolIn.getHeader().equals("Command")) {
                            if(protocolIn.getPayload().getCommand().equals("Login")) {

                            } else {
                                System.out.println("History:");
                                for (Message message : protocolIn.getPayload().getData()) {
                                    System.out.println(message.getName() + " says \"" + message.getMessage() + "\" at " + message.getTime());
                                }
                            }
                        } else {
                            System.out.println(protocolIn.getPayload().getName() + " says \"" + protocolIn.getPayload().getMessage() + "\" at " + protocolIn.getPayload().getTime());
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
            throw new IllegalStateException(e);
        }
    }
}
