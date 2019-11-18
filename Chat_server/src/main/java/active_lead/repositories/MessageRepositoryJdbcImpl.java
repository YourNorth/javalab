package active_lead.repositories;


import active_lead.models.Message;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageRepositoryJdbcImpl extends CrudRepositoryAbstractImpl<Message, Long> implements MessageRepository {

    private ConnectionWrap connectionWrap;

    //language=SQL
    private final String SQL_INSERT_MESSAGE = "insert into " +
            "message (content, name, time) values (?,?,?);";
    //language=SQL
    private final String SQL_UPDATE_MESSAGE = "update message set " +
            "(content,name,time) = (?,?,?) where id = ?;";

    public MessageRepositoryJdbcImpl() { }

    //позволяет преобразовывать строку из бд в объект
    private RowMapper<active_lead.models.Message> userRowMapper = row -> {
        Long id = row.getLong("id");
        String content = row.getString("content");
        String name = row.getString("name");
        String time = row.getString("time");
        return new active_lead.models.Message(id, content,name,time);
    };
    //аналог insert в SQL
    @Override
    public boolean save(active_lead.models.Message model) {
        try {
            PreparedStatement statement = connectionWrap.getConnection().prepareStatement(SQL_INSERT_MESSAGE,
                    Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, model.getContent());
            statement.setString(2, model.getName());
            statement.setString(3, model.getTime());

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
    public void update(active_lead.models.Message model) {
        try {
            PreparedStatement statement = connectionWrap.getConnection().prepareStatement(SQL_UPDATE_MESSAGE,
                    Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, model.getContent());
            statement.setLong(2,model.getId());

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
    public Optional<active_lead.models.Message> find(Long id) {
        active_lead.models.Message user = null;
        try {
            Statement statement = connectionWrap.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("select * from message where id = " + id + ";");

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
    public List<active_lead.models.Message> findAll() {
        List<active_lead.models.Message> result = new ArrayList<>();
        try {
            Statement statement = connectionWrap.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("select * from message");

            while (resultSet.next()) {
                active_lead.models.Message user = userRowMapper.mapRow(resultSet);
                result.add(user);
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return result;
    }

    @Override
    public ArrayList<Message> pagination(Integer size, Integer page) {
        ArrayList<Message> data = new ArrayList<>();
        try {
            Statement statement = connectionWrap.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("select * from message limit " + size + " offset " + page + ";");

            while (resultSet.next()) {
                Message user = userRowMapper.mapRow(resultSet);
                data.add(user);
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return data;
    }
}
