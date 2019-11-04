package die.mass;

import die.mass.models.Message;
import die.mass.models.User;
import die.mass.repositories.MessageRepository;
import die.mass.repositories.MessageRepositoryJdbcImpl;
import die.mass.repositories.UserRepository;
import die.mass.repositories.UserRepositoryJdbcImpl;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DBLoader {

    Connection connection;
    UserRepository userRepository;
    MessageRepository messageRepository;

    public static DBLoader create(String properties) {
        return new DBLoader(properties);
    }

    private DBLoader(String properties) {
        this.connection = connect(properties);
        this.userRepository = new UserRepositoryJdbcImpl(connection);
        this.messageRepository = new MessageRepositoryJdbcImpl(connection);
    }

    public void saveMessage(String content, String name, String time) {
        messageRepository.save(new Message(null, content, name, time));
    }

    public boolean saveUser(String name, String password) {
        userRepository.save(new User(null, name, password));
        return true;
    }

    public String findUser(String name) {
        return userRepository.contains(name);
    }

    private Connection connect(String propertiesName) {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader(propertiesName));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        String username = properties.getProperty("db.username");
        String password = properties.getProperty("db.password");
        String url = properties.getProperty("db.url");

        Connection connection;

        try{
            connection = DriverManager.getConnection(url,username,password);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return connection;
    }

    public ArrayList<Message> getHistory(Integer size, Integer page) {
        return messageRepository.pagination(size,page);
    }
}
