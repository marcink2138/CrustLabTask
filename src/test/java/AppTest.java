import currencyExchange.CurrencyExchangeRates;
import daos.AccountDaoImpl;
import daos.TransactionHistoryDaoImpl;
import daos.UserDaoImpl;
import database.DatabaseManager;
import dtos.AccountBalanceDTO;
import dtos.AccountManipulationDTO;
import dtos.DepositOrCashOutDTO;
import dtos.FilterParamsDTO;
import entities.Account;
import entities.TransactionHistory;
import entities.User;
import enums.Currency;
import enums.TransactionHistoryType;
import interfaces.AccountDAO;
import interfaces.TransactionHistoryDAO;
import interfaces.UserDAO;
import org.junit.jupiter.api.*;
import services.AccountService;
import services.BankAccountManipulationService;
import services.TransactionHistoryService;
import services.UserService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AppTest {
    private static final String firstUserName = "TEST1";
    private static final String secondUserName = "TEST2";
    private static int firstUserId;
    private static int secondUserId;
    private final UserService userService;
    private final TransactionHistoryService transactionHistoryService;
    private final BankAccountManipulationService bankAccountManipulationService;
    private final AccountService accountService;
    private static List<Account> firstUserAccountList;
    private static List<Account> secondUserAccountList;

    public AppTest() {
        DatabaseManager.changeUrlToTestDB();
        TransactionHistoryDAO transactionHistoryDAO = new TransactionHistoryDaoImpl();
        AccountDAO accountDAO = new AccountDaoImpl();
        UserDAO userDAO = new UserDaoImpl();
        this.userService = new UserService(userDAO);
        this.transactionHistoryService = new TransactionHistoryService(transactionHistoryDAO);
        this.bankAccountManipulationService = new BankAccountManipulationService(accountDAO, transactionHistoryDAO);
        this.accountService = new AccountService(accountDAO, transactionHistoryDAO);
    }

    @Test
    @Order(1)
    void createUsers() {
        userService.createUser(firstUserName);
        userService.createUser(secondUserName);
        User firstUser = userService.getUser(firstUserName);
        User secondUser = userService.getUser(secondUserName);
        boolean expression = firstUser.getName().equals(firstUserName) && secondUser.getName().equals(secondUserName)
                && firstUser.getId() != 0 && secondUser.getId() != 0;
        assertTrue(expression);
        firstUserId = firstUser.getId();
        secondUserId = secondUser.getId();
    }

    @Test
    @Order(2)
    void checkDefaultAccounts() {
        firstUserAccountList = accountService.getUserAccounts(firstUserId);
        secondUserAccountList = accountService.getUserAccounts(secondUserId);
        boolean expression = firstUserAccountList.size() == 3 && secondUserAccountList.size() == 3;
        assertTrue(expression);
    }

    @Test
    @Order(3)
    void depositCash() {
        Account firstUserPLNAccount = findAccount(firstUserAccountList, Currency.PLN);
        Account secondUserPLNAccount = findAccount(secondUserAccountList, Currency.PLN);
        DepositOrCashOutDTO firstUserDepositDTO = new DepositOrCashOutDTO(firstUserPLNAccount.getId(), 5000);
        DepositOrCashOutDTO secondUserDepositDTO = new DepositOrCashOutDTO(secondUserPLNAccount.getId(), 5000);
        bankAccountManipulationService.depositCash(firstUserDepositDTO);
        bankAccountManipulationService.depositCash(secondUserDepositDTO);
        firstUserAccountList = accountService.getUserAccounts(firstUserId);
        secondUserAccountList = accountService.getUserAccounts(secondUserId);
        boolean isFirstUserPLNAccountUpdated = isAccountUpdated(firstUserAccountList, Currency.PLN, 5000);
        boolean isSecondUserPLNAccountUpdated = isAccountUpdated(secondUserAccountList, Currency.PLN, 5000);
        assertTrue(isFirstUserPLNAccountUpdated && isSecondUserPLNAccountUpdated);
    }

    @Test
    @Order(4)
    void cashOut() {
        Account firstUserPLNAccount = findAccount(firstUserAccountList, Currency.PLN);
        DepositOrCashOutDTO firstUserCashOutDTO = new DepositOrCashOutDTO(firstUserPLNAccount.getId(), 1000);
        bankAccountManipulationService.cashOut(firstUserCashOutDTO);
        firstUserAccountList = accountService.getUserAccounts(firstUserId);
        boolean isFirstUserPLNAccountUpdated = isAccountUpdated(firstUserAccountList, Currency.PLN, 4000);
        assertTrue(isFirstUserPLNAccountUpdated);
    }

    @Test
    @Order(5)
    void exchangeCash() {
        Account secondUserPLNAccount = findAccount(secondUserAccountList, Currency.PLN);
        Account secondUserUSDAccount = findAccount(secondUserAccountList, Currency.USD);
        AccountManipulationDTO accountManipulationDTO =
                new AccountManipulationDTO(secondUserPLNAccount.getId(), secondUserUSDAccount.getId(), 1000);
        bankAccountManipulationService.exchangeCurrency(accountManipulationDTO);
        secondUserAccountList = accountService.getUserAccounts(secondUserId);
        boolean isSecondUserPLNAccountUpdated = isAccountUpdated(secondUserAccountList, Currency.PLN, 4000);
        double exchangeRate = CurrencyExchangeRates.getProperExchangeRate(Currency.PLN, Currency.USD);
        boolean isSecondUserUSDAccountUpdated = isAccountUpdated(secondUserAccountList, Currency.USD, 1000 * exchangeRate);
        assertTrue(isSecondUserUSDAccountUpdated && isSecondUserPLNAccountUpdated);
    }

    @Test
    @Order(6)
    void transferCashBetweenUsers() {
        Account firstUserPLNAccount = findAccount(firstUserAccountList, Currency.PLN);
        Account secondUserPLNAccount = findAccount(secondUserAccountList, Currency.PLN);
        AccountManipulationDTO accountManipulationDTO =
                new AccountManipulationDTO(firstUserPLNAccount.getId(), secondUserPLNAccount.getId(), 1000);
        bankAccountManipulationService.transferCashBetweenUsers(accountManipulationDTO);
        firstUserAccountList = accountService.getUserAccounts(firstUserId);
        secondUserAccountList = accountService.getUserAccounts(secondUserId);
        boolean isFirstUserPLNAccountUpdated = isAccountUpdated(firstUserAccountList, Currency.PLN, 3000);
        boolean isSecondUserPLNAccountUpdated = isAccountUpdated(secondUserAccountList, Currency.PLN, 5000);
        assertTrue(isFirstUserPLNAccountUpdated && isSecondUserPLNAccountUpdated);
    }

    @Test
    @Order(7)
    void getAccountBalanceWithTransactionHistory() {
        Account firstUserPLNAccount = findAccount(firstUserAccountList, Currency.PLN);
        FilterParamsDTO filterParamsDTO = new FilterParamsDTO(firstUserId, firstUserPLNAccount.getId(), null, null, null, null);
        AccountBalanceDTO accountBalanceDTO = accountService.getAccountBalanceWithHistory(filterParamsDTO);
        boolean isBalanceCorrect = accountBalanceDTO.getAmount() == 3000;
        boolean isTransactionHistoryCorrect = accountBalanceDTO.getTransactionHistoryList().get(0).getType() == TransactionHistoryType.CASH_DEPOSIT &&
                accountBalanceDTO.getTransactionHistoryList().get(1).getType() == TransactionHistoryType.CASH_OUT &&
                accountBalanceDTO.getTransactionHistoryList().get(2).getType() == TransactionHistoryType.CASH_TRANSFER;
        assertTrue(isBalanceCorrect && isTransactionHistoryCorrect);
    }

    @Test
    @Order(8)
    void getTransactionHistoryFilteredByTransactionType() {
        FilterParamsDTO filterParamsDTO = new FilterParamsDTO(secondUserId, null, null, TransactionHistoryType.CURRENCY_EXCHANGE, null, null);
        List<TransactionHistory> transactionHistoryList = transactionHistoryService.getTransactionHistory(filterParamsDTO);
        boolean isFilteredTransactionHistoryCorrect = transactionHistoryList.size() == 2 &&
                transactionHistoryList.stream().filter(transaction -> transaction.getType() == TransactionHistoryType.CURRENCY_EXCHANGE).count() == 2;
        assertTrue(isFilteredTransactionHistoryCorrect);
    }

    @AfterAll
    @BeforeAll
    static void cleanTables() {
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = conn.prepareStatement("DELETE FROM users;");
            preparedStatement.executeUpdate();
            DatabaseManager.closePreparedStatement(preparedStatement);
            preparedStatement = conn.prepareStatement("DELETE FROM accounts;");
            preparedStatement.executeUpdate();
            DatabaseManager.closePreparedStatement(preparedStatement);
            preparedStatement = conn.prepareStatement("DELETE FROM transactions_history;");
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.closePreparedStatement(preparedStatement);
            DatabaseManager.closeConnection(conn);
        }
    }

    private boolean isAccountUpdated(List<Account> userAccountList, Currency currency, double amount) {
        return userAccountList.stream()
                .anyMatch(account -> account.getCurrency() == currency && account.getAmount() == amount);
    }

    private Account findAccount(List<Account> secondUserAccountList, Currency currency) {
        return secondUserAccountList.stream()
                .filter(account -> account.getCurrency() == currency)
                .findFirst()
                .orElseThrow(() -> new RuntimeException(String.format("%s account not found!", currency.toString())));
    }

}
