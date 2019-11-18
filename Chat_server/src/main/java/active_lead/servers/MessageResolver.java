package active_lead.servers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import active_lead.protocol.Protocol;
import active_lead.services.CommandService;
import active_lead.services.LoginService;
import active_lead.services.MessageService;
import active_lead.services.ResponseService;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;

public class MessageResolver {

    private List<ChatMultiServer.ClientHandler> clients;
    private Protocol protocolIn;
    private Protocol protocolOut;
    private ObjectMapper objectMapper;
    private ServerContext serverContext;
    private LoginService loginService;
    private ResponseService responseService;
    private CommandService commandService;
    private GetTokenServiceImpl getTokenService;
    private MessageService messageService;


    public MessageResolver(ServerContext serverContext, List<ChatMultiServer.ClientHandler> clients) {
        this.clients = clients;
        this.serverContext = serverContext;
        this.protocolIn = new Protocol();
        this.protocolOut = new Protocol();
        this.objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        this.loginService = (LoginService) serverContext.getByName("LoginService");
        this.responseService = (ResponseService) serverContext.getByName("ResponseService");
        this.commandService = (CommandService) serverContext.getByName("CommandService");
        this.messageService = (MessageService) serverContext.getByName("MessageService");
        this.getTokenService =  (GetTokenServiceImpl) serverContext.getByName("GetTokenServiceImpl");

    }

    public String successfullyEntering(String inputLine, PrintWriter out) {
        try {
            System.out.println(inputLine);
            protocolIn = objectMapper.readValue(inputLine, Protocol.class);
            return loginService.parse(protocolIn, out);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public String listen(String inputLine) {
        try {
            protocolIn = objectMapper.readValue(inputLine, Protocol.class);
            String header = protocolIn.getHeader();
            LocalDateTime localDateTime = LocalDateTime.now();
            switch (header) {
                case "LogOut":
                    // бегаем по всем клиентам и обовещаем их о событии
                    responseService.responseLogout(protocolIn, protocolOut, localDateTime, clients);
                    return "delete";
                case "Command":
                    responseService.answering(commandService.createOut(protocolIn),
                            getTokenService.getData(protocolIn.getPayload().getToken(), "name"), clients);
                    break;
                default:
                    System.out.println(inputLine);
                    protocolOut = messageService.createOut(localDateTime, protocolIn);
                    responseService.responseMessage(protocolOut, clients);
                    break;
            }
            return "";
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
