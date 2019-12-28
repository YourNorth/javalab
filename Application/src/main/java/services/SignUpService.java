package services;


import models.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import repositories.UserRepository;

public class SignUpService implements Service {

    private UserRepository userRepository;
    private PasswordEncoder encoder = new BCryptPasswordEncoder();

    public boolean signUp(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public boolean signIn(String name, String password) {
        return encoder.matches(password, userRepository.getPassword(name));
    }

}
