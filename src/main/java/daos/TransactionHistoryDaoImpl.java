package daos;

import database.DatabaseManager;
import dtos.FilterParamsDTO;
import entities.TransactionHistory;
import enums.TransactionHistoryType;
import interfaces.TransactionHistoryDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class TransactionHistoryDaoImpl implements TransactionHistoryDAO {
    private static final String INSERT_QUERY = "INSERT INTO transactions_history (amount, type, date, accountId) VALUES(?,?,?,?)";
    private static final String BASIC_SELECT_QUERY = "SELECT * FROM transactions_history WHERE accountId IN (SELECT accounts.id FROM accounts WHERE accounts.userId = ?)";
    private static final String ACCOUNT_ID = "accountId";
    private static final String AMOUNT = "amount";
    private static final String DATE = "date";
    private static final String TYPE = "type";
    private static final String ID = "id";

    @Override
    public void save(TransactionHistory transactionHistory) {
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = conn.prepareStatement(INSERT_QUERY);
            preparedStatement.setDouble(1, transactionHistory.getAmount());
            preparedStatement.setString(2, transactionHistory.getType().toString());
            preparedStatement.setLong(3, transactionHistory.getDate().getTime());
            preparedStatement.setInt(4, transactionHistory.getAccountId());
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.closePreparedStatement(preparedStatement);
            DatabaseManager.closeConnection(conn);
        }
    }

    @Override
    public List<TransactionHistory> findAllByFilterParameters(FilterParamsDTO filterParamsDTO) {
        String queryWithFilters = getSelectQueryString(filterParamsDTO);
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement preparedStatement = null;
        List<TransactionHistory> transactionHistoryList = new LinkedList<>();
        try {
            preparedStatement = conn.prepareStatement(queryWithFilters);
            setPreparedStatementsParams(preparedStatement, filterParamsDTO);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                TransactionHistory transactionHistory = getTransaction(rs);
                transactionHistoryList.add(transactionHistory);
            }
            return transactionHistoryList;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.closePreparedStatement(preparedStatement);
            DatabaseManager.closeConnection(conn);
        }
        return transactionHistoryList;
    }

    private void setPreparedStatementsParams(PreparedStatement preparedStatement, FilterParamsDTO filterParamsDTO) throws SQLException {
        int parameterIndex = 1;
        preparedStatement.setInt(parameterIndex, filterParamsDTO.getUserId());
        parameterIndex++;
        if (filterParamsDTO.getAccountId() != null) {
            preparedStatement.setInt(parameterIndex, filterParamsDTO.getAccountId());
            parameterIndex++;
        }
        if (filterParamsDTO.getCurrency() != null) {
            preparedStatement.setString(parameterIndex, filterParamsDTO.getCurrency().toString());
            parameterIndex++;
        }
        if (filterParamsDTO.getTransactionHistoryType() != null) {
            preparedStatement.setString(parameterIndex, filterParamsDTO.getTransactionHistoryType().toString());
            parameterIndex++;
        }
        if (filterParamsDTO.getFromDate() != null && filterParamsDTO.getToDate() != null) {
            long fromDateLong = filterParamsDTO.getFromDate().getTime();
            long toDateLong = filterParamsDTO.getToDate().getTime();
            preparedStatement.setLong(parameterIndex, fromDateLong);
            parameterIndex++;
            preparedStatement.setLong(parameterIndex, toDateLong);
        }
    }

    private String getSelectQueryString(FilterParamsDTO filterParamsDTO) {
        StringBuilder sb = new StringBuilder(BASIC_SELECT_QUERY);
        if (filterParamsDTO.getUserId() == null)
            throw new RuntimeException("User id not provided!");
        if (filterParamsDTO.getAccountId() != null)
            sb.append(" AND accountId = ?");
        if (filterParamsDTO.getCurrency() != null)
            sb.append(" AND currency = ?");
        if (filterParamsDTO.getTransactionHistoryType() != null)
            sb.append(" AND type = ?");
        if (filterParamsDTO.getFromDate() != null && filterParamsDTO.getToDate() != null)
            sb.append(" AND date BETWEEN ? AND ?");
        sb.append(" ORDER BY date");
        return sb.toString();
    }

    private TransactionHistory getTransaction(ResultSet rs) throws SQLException {
        return TransactionHistory.builder()
                .accountId(rs.getInt(ACCOUNT_ID))
                .amount(rs.getDouble(AMOUNT))
                .date(new Date(rs.getLong(DATE)))
                .type(Enum.valueOf(TransactionHistoryType.class, rs.getString(TYPE)))
                .id(rs.getInt(ID))
                .build();
    }

}
