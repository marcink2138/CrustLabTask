package daos;

import database.DatabaseManager;
import entities.User;
import interfaces.UserDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDaoImpl implements UserDAO {
    private static final String SELECT_BY_NAME_QUERY = "SELECT * FROM users WHERE name = ?";
    private static final String INSERT_NEW_USER = "INSERT INTO users (name) VALUES (?)";
    private static final String ID = "id";
    private static final String NAME = "name";

    @Override
    public User findByName(String name) {
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = conn.prepareStatement(SELECT_BY_NAME_QUERY);
            preparedStatement.setString(1, name);
            ResultSet rs = preparedStatement.executeQuery();
            User user = new User();
            while (rs.next()) {
                user.setId(rs.getInt(ID));
                user.setName(rs.getString(NAME));
            }
            return user;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.closePreparedStatement(preparedStatement);
            DatabaseManager.closeConnection(conn);
        }
        return null;
    }

    @Override
    public void save(User user) {
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = conn.prepareStatement(INSERT_NEW_USER);
            preparedStatement.setString(1, user.getName());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.closePreparedStatement(preparedStatement);
            DatabaseManager.closeConnection(conn);
        }
    }
}
