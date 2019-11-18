package die.mass.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import die.mass.protocol.Payload;
import die.mass.protocol.Protocol;
import die.mass.servers.ChatMultiServer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class ResponseService implements Service {

    private ObjectMapper objectMapper;

    public ResponseService() {
        this.objectMapper = new ObjectMapper();
    }

    public void responseLogout(Protocol protocolIn, Protocol protocolOut, LocalDateTime localDateTime, List<ChatMultiServer.ClientHandler> clients) {
        protocolOut.setHeader("LogOut");
        protocolOut.setPayload(new Payload());
        protocolOut.getPayload().setName(protocolIn.getPayload().getName());
        protocolOut.getPayload().setTime(localDateTime.toString());
        try {
            System.out.println(objectMapper.writeValueAsString(protocolOut));
            String answer = objectMapper.writeValueAsString(protocolOut);
            for(ChatMultiServer.ClientHandler clientHandler : clients) {
                clientHandler.getOut().println(answer);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void responseMessage(Protocol protocolOut, List<ChatMultiServer.ClientHandler> clients) {
        try {
            String answer = objectMapper.writeValueAsString(protocolOut);
            for(ChatMultiServer.ClientHandler clientHandler : clients) {
                clientHandler.getOut().println(answer);
            }
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void answering(Protocol protocolOut, String name, List<ChatMultiServer.ClientHandler> clients) {
        for(ChatMultiServer.ClientHandler clientHandler : clients) {
            if(clientHandler.getClientName().equals(name)) {
                try {
                    System.out.println("answer to client " + clientHandler.getClientName());
                    clientHandler.getOut().println(objectMapper.writeValueAsString(protocolOut));
                } catch (JsonProcessingException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }
    }
}
