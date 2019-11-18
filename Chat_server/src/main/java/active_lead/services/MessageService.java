package die.mass.services;

import die.mass.models.Message;
import die.mass.protocol.Payload;
import die.mass.protocol.Protocol;
import die.mass.repositories.MessageRepository;
import die.mass.servers.Component;
import die.mass.servers.GetTokenServiceImpl;

import java.time.LocalDateTime;

public class MessageService implements Service {

    private Protocol protocolIn = new Protocol();
    private Protocol protocolOut = new Protocol();
    private MessageRepository messageRepository;
    private GetTokenServiceImpl getTokenService;

    public MessageService() { }

    @Override
    public <Q extends Component> void setField(Q object) {
        if(MessageRepository.class.isAssignableFrom(object.getClass())) {
            this.messageRepository = (MessageRepository) object;
        } else if(GetTokenServiceImpl.class.isAssignableFrom(object.getClass())) {
            this.getTokenService = (GetTokenServiceImpl) object;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public Protocol createOut(LocalDateTime localDateTime, Protocol protocolIn) {
        String message = protocolIn.getPayload().getMessage();
        protocolOut.setPayload(new Payload());
        protocolOut.getPayload().setName(protocolIn.getPayload().getName());
        protocolOut.getPayload().setTime(localDateTime.toString());
        if (message == null) {
            protocolOut.setHeader("Login");
        } else {
            String name = getTokenService.getData(protocolIn.getPayload().getToken(), "name");
            messageRepository.save(new Message(null, message, name, localDateTime.toString()));
            protocolOut.setHeader("Message");
            protocolOut.getPayload().setMessage(protocolIn.getPayload().getMessage());
            protocolOut.getPayload().setName(name);
        }
        return protocolOut;
    }

}
