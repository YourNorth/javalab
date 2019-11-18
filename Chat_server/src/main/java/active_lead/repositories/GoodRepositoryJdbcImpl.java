package active_lead.repositories;

import active_lead.models.Good;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GoodRepositoryJdbcImpl extends CrudRepositoryAbstractImpl<Good, Long> implements GoodRepository {

    private ConnectionWrap connectionWrap;

    private final String tableName = "good";
    //language=SQL
    private final String SQL_INSERT_GOOD = "insert into " + tableName +
            " (name, price) values (?, ?);";
    //language=SQL
    private final String SQL_UPDATE_GOOD = "update " + tableName + " set " +
            "(name, price) = (?, ?) where id = ?;";
    //language=SQL
    private final String SQL_DELETE_GOOD = "DELETE FROM " + tableName +
            " where id = ?;";

    public GoodRepositoryJdbcImpl() { }

    //позволяет преобразовывать строку из бд в объект
    private RowMapper<Good> userRowMapper = row -> {
        Long id = row.getLong("id");
        String name = row.getString("name");
        Integer price = row.getInt("price");
        return new Good(id, name, price);
    };

    @Override
    public boolean save(Good model) {
        try {
            PreparedStatement statement = connectionWrap.getConnection().prepareStatement(SQL_INSERT_GOOD,
                    Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, model.getName());
            statement.setInt(2, model.getPrice());

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

    @Override
    public void update(Good model) {

    }

    @Override
    public void delete(Long aLong) {
        try (PreparedStatement statement = connectionWrap.getConnection().prepareStatement(SQL_DELETE_GOOD, Statement.RETURN_GENERATED_KEYS)) {
            // Добавление значений параметров 1-4 в SQL апросе
            statement.setLong(1, aLong);
            // Выполнение запроса
            int affectedRows = statement.executeUpdate();
            // Если не была затронута ни одна строка, значит произошла какая-то ошибка при выполнении запроса
            if (affectedRows == 0) {
                System.err.println(":(");
            }
            // Ключ нового пользователя
            ResultSet generatesKeys = statement.getGeneratedKeys();
            // Добавление id объекту пользователя
            generatesKeys.close();
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public Optional<Good> find(Long aLong) {
        return Optional.empty();
    }

    @Override
    public List<Good> findAll() {
        List<Good> result = new ArrayList<>();
        try {
            Statement statement = connectionWrap.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("select * from " + tableName + ";");

            while (resultSet.next()) {
                Good good = userRowMapper.mapRow(resultSet);
                result.add(good);
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return result;
    }
}
