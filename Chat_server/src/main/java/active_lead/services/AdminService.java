package die.mass.services;

import die.mass.protocol.Protocol;
import die.mass.servers.Component;
import die.mass.servers.GetTokenServiceImpl;

public class AdminService implements Service {

    private GetTokenServiceImpl getTokenService;

    public AdminService() { }


    public boolean isAdmin(Protocol protocolIn) {
        return getTokenService.getData(protocolIn.getPayload().getToken(), "role").equals("1");
    }

    @Override
    public <Q extends Component> void setField(Q object) {
        if(GetTokenServiceImpl.class.isAssignableFrom(object.getClass())) {
            this.getTokenService = (GetTokenServiceImpl) object;
        } else {
            throw new IllegalArgumentException();
        }
    }


}
