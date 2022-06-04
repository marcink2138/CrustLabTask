package daos;

import database.DatabaseManager;
import entities.Account;
import enums.Currency;
import interfaces.AccountDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountDaoImpl implements AccountDAO {
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM accounts WHERE id = ?";
    private static final String FIND_BY_USER_ID_QUERY = "SELECT * FROM accounts WHERE userId = ?";
    private static final String UPDATE_QUERY = "UPDATE accounts SET amount = ? WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO accounts (currency, amount, userId) VALUES(?,?,?)";
    private static final String ID = "id";
    private static final String CURRENCY = "currency";
    private static final String AMOUNT = "amount";
    private static final String USER_ID = "userId";

    @Override
    public void save(Account account) {
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = conn.prepareStatement(INSERT_QUERY);
            preparedStatement.setString(1, account.getCurrency().toString());
            preparedStatement.setDouble(2, account.getAmount());
            preparedStatement.setInt(3, account.getUserId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.closePreparedStatement(preparedStatement);
            DatabaseManager.closeConnection(conn);
        }

    }

    @Override
    public void update(Account account) {
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = conn.prepareStatement(UPDATE_QUERY);
            preparedStatement.setDouble(1, account.getAmount());
            preparedStatement.setInt(2, account.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.closePreparedStatement(preparedStatement);
            DatabaseManager.closeConnection(conn);
        }
    }

    @Override
    public Account findById(int id) {
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = conn.prepareStatement(FIND_BY_ID_QUERY);
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            Account account = new Account();
            while (rs.next()) {
                account.setId(rs.getInt(ID));
                Currency currency = Enum.valueOf(Currency.class, rs.getString(CURRENCY));
                account.setCurrency(currency);
                account.setAmount(rs.getDouble(AMOUNT));
                account.setUserId(rs.getInt(USER_ID));
            }
            return account;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.closePreparedStatement(preparedStatement);
            DatabaseManager.closeConnection(conn);
        }
        return null;
    }

    @Override
    public List<Account> findByUserId(int userId) {
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = conn.prepareStatement(FIND_BY_USER_ID_QUERY);
            preparedStatement.setInt(1, userId);
            ResultSet rs = preparedStatement.executeQuery();
            List<Account> accountList = new ArrayList<>();
            while (rs.next()) {
                Account account = new Account();
                account.setId(rs.getInt(ID));
                Currency currency = Enum.valueOf(Currency.class, rs.getString(CURRENCY));
                account.setCurrency(currency);
                account.setAmount(rs.getDouble(AMOUNT));
                account.setUserId(rs.getInt(USER_ID));
                accountList.add(account);
            }
            return accountList;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.closePreparedStatement(preparedStatement);
            DatabaseManager.closeConnection(conn);
        }
        return null;
    }

}
