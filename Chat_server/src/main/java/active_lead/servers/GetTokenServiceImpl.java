package active_lead.servers;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import org.springframework.security.authentication.AuthenticationServiceException;

import java.util.*;

public class GetTokenServiceImpl implements Component {

    final String key = "qwerty007";

    public GetTokenServiceImpl() { }

    public String getToken(String username, String password, String role) {
        if (username == null || password == null) return null;
        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("role", role);
        tokenData.put("name", username);
        tokenData.put("password",password);
        JwtBuilder jwtBuilder = Jwts.builder();
        jwtBuilder.setClaims(tokenData);
        String token = jwtBuilder.signWith(SignatureAlgorithm.HS256, key).compact();
        return token;
    }

    public String getData(String token, String field) {
        DefaultClaims claims;
        try {
            claims = (DefaultClaims) Jwts.parser().setSigningKey(key).parse(token).getBody();
        } catch (Exception ex) {
            throw new AuthenticationServiceException("Token corrupted");
        }
        return (String)claims.get(field);
    }
}