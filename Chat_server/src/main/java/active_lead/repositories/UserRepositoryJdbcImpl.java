package active_lead.repositories;

import active_lead.models.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepositoryJdbcImpl extends CrudRepositoryAbstractImpl<User, Long> implements UserRepository {

    private ConnectionWrap connectionWrap;

    @Override
    public ConnectionWrap getConnectionWrap() {
        return connectionWrap;
    }

    //language=SQL
    private final String SQL_INSERT_USER = "insert into " +
            "account (name, password) values (?, ?);";
    //language=SQL
    private final String SQL_UPDATE_USER = "update account set " +
            "(name, password) = (?, ?) where id = ?;";

    public UserRepositoryJdbcImpl() { }

    //позволяет преобразовывать строку из бд в объект
    private RowMapper<active_lead.models.User> userRowMapper = row -> {
        Long id = row.getLong("id");
        String name = row.getString("name");
        String password = row.getString("password");
        Boolean isAdmin = row.getBoolean("isadmin");
        return new User(id, name, password, isAdmin);
    };
    //аналог insert в SQL
    @Override
    public boolean save(User model) {
        try {
            PreparedStatement statement = connectionWrap.getConnection().prepareStatement(SQL_INSERT_USER,
                    Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, model.getName());
            statement.setString(2, model.getPassword());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException();
            }

            ResultSet generatesKeys = statement.getGeneratedKeys();

            if (generatesKeys.next()) {
                model.setId(generatesKeys.getLong("id"));
            } else {
                throw new SQLException();
            }
            statement.close();
            generatesKeys.close();
            return true;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    //аналог update в SQL
    @Override
    public void update(User model) {
        try {
            PreparedStatement statement = connectionWrap.getConnection().prepareStatement(SQL_UPDATE_USER,
                    Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, model.getName());
            statement.setString(2, model.getPassword());
            statement.setLong(3,model.getId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException();
            }

        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
    //аналог delete в SQL
    @Override
    public void delete(Long id) {
        try {
            Statement statement = connectionWrap.getConnection().createStatement();
            statement.execute("delete from account where id = " + id + ";");
            System.out.println("Deleted is comleted");
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
    //аналог select-а по id SQL
    @Override
    public Optional<active_lead.models.User> find(Long id) {
        active_lead.models.User user = null;
        try {
            Statement statement = connectionWrap.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("select * from account where id = " + id + ";");

            if (resultSet.next()) {
                user = userRowMapper.mapRow(resultSet);
            }
            statement.close();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return Optional.ofNullable(user);
    }

    @Override
    public List<active_lead.models.User> findAll() {
        List<active_lead.models.User> result = new ArrayList<>();
        try {
            Statement statement = connectionWrap.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("select * from account");

            while (resultSet.next()) {
                active_lead.models.User user = userRowMapper.mapRow(resultSet);
                result.add(user);
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return result;
    }

    @Override
    public String contains(String name) {
        active_lead.models.User user = null;
        try {
            Statement statement = connectionWrap.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("select * from account where name = '" + name + "';");

            if (resultSet.next()) {
                user = userRowMapper.mapRow(resultSet);
            }
            statement.close();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return user == null ? null : user.getPassword();
    }
}
