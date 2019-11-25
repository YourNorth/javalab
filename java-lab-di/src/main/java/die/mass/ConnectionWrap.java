package active_lead;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

public class ConnectionWrap implements Component {

    private Connection connection;

    public ConnectionWrap(String pathToProperties) {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader(pathToProperties));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        String username = properties.getProperty("db.username");
        String password = properties.getProperty("db.password");
        String url = properties.getProperty("db.url");
        ArrayList<ReflectionContext> a;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException | ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
