package die.mass.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import die.mass.dto.UserAuthDTO;
import die.mass.models.User;
import die.mass.protocol.Payload;
import die.mass.protocol.Protocol;
import die.mass.repositories.UserRepository;
import die.mass.servers.Component;
import die.mass.servers.GetTokenServiceImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.io.PrintWriter;

public class LoginService implements Service{

    private Protocol protocolIn;
    private Protocol protocolOut = new Protocol();
    private UserRepository userRepository;
    private GetTokenServiceImpl getTokenService;
    private ObjectMapper objectMapper;
    private PrintWriter out;
    private PasswordEncoder encoder;

    public LoginService() {
        this.encoder = new BCryptPasswordEncoder();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public <Q extends Component> void setField(Q object) {
        if(UserRepository.class.isAssignableFrom(object.getClass())) {
            this.userRepository = (UserRepository) object;
        } else if(GetTokenServiceImpl.class.isAssignableFrom(object.getClass())) {
            this.getTokenService = (GetTokenServiceImpl) object;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public String parse(Protocol protocolIn, PrintWriter out) {
        this.out = out;
        this.protocolIn = protocolIn;
        String name = "";
        boolean f;
            if (protocolIn.getHeader().equals("SignUp")) {
                f = checkToNotExistsWithLogin();
                if (f) {
                    name = protocolIn.getPayload().getName();
                    saveUser(UserAuthDTO.newBuilder().setName(name).setIsAdmin(protocolIn.getPayload().getRole().equals("1")).
                            setPassword(protocolIn.getPayload().getPassword()).build());
                    checkToExistsWithLogin();
                }
            } else if(protocolIn.getHeader().equals("Login")) {
                if (protocolIn.getPayload().getTokenExists()) {
                    f = checkToExistsWithToken();
                    name = getTokenService.getData(protocolIn.getPayload().getToken(), "name");
                } else {
                    f = checkToExistsWithLogin();
                    if (f) {
                        name = protocolIn.getPayload().getName();
                        protocolOut.getPayload().setToken(getTokenService.getToken(protocolIn.getPayload().getName(), protocolIn.getPayload().getPassword(), protocolIn.getPayload().getRole()));
                    }
                }
            } else {
                System.err.println("Ancorrect header");
                throw new IllegalArgumentException();
            }
            return f ? name : null;
//        System.out.println("New client " + protocolIn.getPayload().getName());

    }

    private boolean checkToNotExistsWithLogin() {
        String name = protocolIn.getPayload().getName();
        String password = protocolIn.getPayload().getPassword();
        String role = protocolIn.getPayload().getRole();
        boolean f = !accountExists(name, password);
        protocolOut.setHeader("Command");
        protocolOut.setPayload(new Payload());
        protocolOut.getPayload().setCommand("Login");
        protocolOut.getPayload().setCorrect(f);
        if (f) protocolOut.getPayload().setToken(getTokenService.getToken(name, password, role));
        try {
//                System.out.println("SERVERLOG: checkToNotExists() " + objectMapper.writeValueAsString(protocolOut));
            out.println(objectMapper.writeValueAsString(protocolOut));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
        return f;
    }

    private boolean checkToExistsWithLogin() {
        return checkToExists(protocolIn.getPayload().getName(), protocolIn.getPayload().getPassword());
    }

    private boolean checkToExistsWithToken() {
        String token = protocolIn.getPayload().getToken();
        return checkToExists(getTokenService.getData(token, "name"), getTokenService.getData(token, "password"));
    }

    private boolean checkToExists(String name, String password) {
        boolean f = accountExists(name, password);
        protocolOut.setPayload(new Payload());
        protocolOut.setHeader("Command");
        protocolOut.getPayload().setCommand("Login");
        protocolOut.getPayload().setCorrect(f);

        try {
                System.out.println("SERVERLOG: checkToExists() " + objectMapper.writeValueAsString(protocolOut));
            out.println(objectMapper.writeValueAsString(protocolOut));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return f;
    }

    private boolean accountExists(String name, String password) {
        String p = userRepository.contains(name);
        return encoder.matches(password, p);
    }


    private boolean saveUser(UserAuthDTO dto) {
        return userRepository.save(new User(null,dto.getName(), encoder.encode(dto.getPassword()), dto.getAdmin()));
    }
}
