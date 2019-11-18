package die.mass.services;

import die.mass.models.Good;
import die.mass.models.Message;
import die.mass.protocol.Payload;
import die.mass.protocol.Protocol;
import die.mass.repositories.GoodRepository;
import die.mass.repositories.MessageRepository;
import die.mass.servers.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandService implements Service{

    private MessageRepository messageRepository;
    private GoodRepository goodRepository;
    private AdminService adminService;

    public CommandService() { }

    @Override
    public <Q extends Component> void setField(Q object) {
        if(MessageRepository.class.isAssignableFrom(object.getClass())) {
            this.messageRepository = (MessageRepository) object;
        } else if(GoodRepository.class.isAssignableFrom(object.getClass())) {
            this.goodRepository = (GoodRepository) object;
        } else if(AdminService.class.isAssignableFrom(object.getClass())) {
            this.adminService = (AdminService) object;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public Protocol createOut(Protocol protocolIn) {
        String command = protocolIn.getPayload().getCommand();
        switch (command) {
            case "history":
                return getHistory(protocolIn);
            case "showGoods":
                return showGoods();
            case "buy":
                return buy(protocolIn.getPayload().getId());
            case "addGood":
                return addGood(protocolIn);
        }
        return null;
    }

    public Protocol getHistory(Protocol protocolIn) {
        Integer page = protocolIn.getPayload().getPage();
        Integer size = protocolIn.getPayload().getSize();
        Protocol protocolOut = new Protocol();
        protocolOut.setHeader("Command");
        protocolOut.setPayload(new Payload());
        protocolOut.getPayload().setCommand("history");
        protocolOut.getPayload().setData((ArrayList<Payload>) getHistoryList(size, page).stream().
                map(i -> new Payload(i.getName(), i.getContent(), i.getTime())).collect(Collectors.toList()));
        return protocolOut;
    }

    private ArrayList<Message> getHistoryList(Integer size, Integer page) {
        return messageRepository.pagination(size,page);
    }

    private List<Good> getGoodList() {
        return goodRepository.findAll();
    }

    private Protocol showGoods() {
        Protocol protocolOut = new Protocol();
        protocolOut.setHeader("Command");
        protocolOut.setPayload(new Payload());
        protocolOut.getPayload().setCommand("showGoods");
        protocolOut.getPayload().setData((ArrayList<Payload>) getGoodList().stream().
                map(i -> new die.mass.protocol.Payload(i.getId(),i.getName(), i.getPrice())).collect(Collectors.toList()));
        return protocolOut;
    }

    private Protocol buy(Long id) {
        goodRepository.delete(id);
        return sendMessage("");
    }

    private Protocol addGood(Protocol protocolIn) {
        if(adminService.isAdmin(protocolIn)) {
            Good good = new Good(null, protocolIn.getPayload().getName(), protocolIn.getPayload().getPrice());
            goodRepository.save(good);
            return sendMessage("Good" + good.getName() + " with price " + good.getPrice() + " added");
        }
        return sendMessage("You're not admin, please get this role and try again");
    }

    private Protocol sendMessage(String message) {
        Protocol protocolOut = new Protocol();
        protocolOut.setHeader("Command");
        protocolOut.setPayload(new Payload());
        protocolOut.getPayload().setCommand("message");
        protocolOut.getPayload().setMessage(message);
        return protocolOut;
    }
}
