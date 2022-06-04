package services;

import dtos.AccountBalanceDTO;
import dtos.FilterParamsDTO;
import entities.Account;
import entities.TransactionHistory;
import interfaces.AccountDAO;
import interfaces.TransactionHistoryDAO;

import java.util.List;

public class AccountService {
    private AccountDAO accountDAO;
    private TransactionHistoryDAO transactionHistoryDAO;

    public AccountService(AccountDAO accountDAO, TransactionHistoryDAO transactionHistoryDAO) {
        this.accountDAO = accountDAO;
        this.transactionHistoryDAO = transactionHistoryDAO;
    }

    public AccountBalanceDTO getAccountBalanceWithHistory(FilterParamsDTO filterParamsDTO) {
        if (filterParamsDTO.getUserId() == null)
            throw new RuntimeException("User id have to be provided!");
        if (filterParamsDTO.getAccountId() == null)
            throw new RuntimeException("Account id have to be provided!");
        List<TransactionHistory> transactionHistoryList = transactionHistoryDAO.findAllByFilterParameters(filterParamsDTO);
        Account account = accountDAO.findById(filterParamsDTO.getAccountId());
        return new AccountBalanceDTO(account.getAmount(), transactionHistoryList);
    }

    public List<Account> getUserAccounts(int userId) {
        return accountDAO.findByUserId(userId);
    }

}
