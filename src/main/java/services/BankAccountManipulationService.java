package services;

import currencyExchange.CurrencyExchangeRates;
import dtos.AccountManipulationDTO;
import dtos.AccountsAmountChangeDTO;
import dtos.DepositOrCashOutDTO;
import entities.Account;
import entities.TransactionHistory;
import enums.TransactionHistoryType;
import interfaces.AccountDAO;
import interfaces.TransactionHistoryDAO;

import java.util.Date;

public class BankAccountManipulationService {
    private AccountDAO accountDAO;
    private TransactionHistoryDAO transactionHistoryDAO;

    public BankAccountManipulationService(AccountDAO accountDAO, TransactionHistoryDAO transactionHistoryDAO) {
        this.accountDAO = accountDAO;
        this.transactionHistoryDAO = transactionHistoryDAO;
    }

    public void exchangeCurrency(AccountManipulationDTO accountManipulationDTO) {
        if (accountManipulationDTO.getAmount() <= 0)
            throw new RuntimeException("You cannot exchange amount which equals 0 or is negative number!");
        Account source = getAccount(accountManipulationDTO.getSourceAccountId());
        if (source.getAmount() < accountManipulationDTO.getAmount())
            throw new RuntimeException("Not enough founds!");
        Account destination = getAccount(accountManipulationDTO.getDestinationAccountId());
        if (source.getUserId() != destination.getUserId())
            throw new RuntimeException("Currency exchange is possible only between accounts belonging to the same user!");
        AccountsAmountChangeDTO amountChangeDTO = calculateCurrencyExchange(accountManipulationDTO, source, destination);
        updateAccountsAndCreateHistory(source, destination, amountChangeDTO, TransactionHistoryType.CURRENCY_EXCHANGE);
    }

    public void transferCashBetweenUsers(AccountManipulationDTO accountManipulationDTO) {
        Account source = getAccount(accountManipulationDTO.getSourceAccountId());
        Account destination = getAccount(accountManipulationDTO.getDestinationAccountId());
        if (source.getUserId() == destination.getUserId())
            throw new RuntimeException("Illegal cash transfer");
        if (source.getCurrency() != destination.getCurrency())
            throw new RuntimeException("You are trying to account with different currency!");
        if (source.getAmount() < accountManipulationDTO.getAmount())
            throw new RuntimeException("Not enough founds!");
        AccountsAmountChangeDTO amountChangeDTO = calculateCashTransfer(accountManipulationDTO, source, destination);
        updateAccountsAndCreateHistory(source, destination, amountChangeDTO, TransactionHistoryType.CASH_TRANSFER);
    }

    public void depositCash(DepositOrCashOutDTO depositCashDTO) {
        Account account = getAccount(depositCashDTO.getAccountId());
        var amountChange = depositCashDTO.getAmount();
        var updatedAmount = account.getAmount() + amountChange;
        account.setAmount(updatedAmount);
        accountDAO.update(account);
        TransactionHistory transactionHistory = getTransaction(account, amountChange, TransactionHistoryType.CASH_DEPOSIT);
        transactionHistoryDAO.save(transactionHistory);
    }

    public void cashOut(DepositOrCashOutDTO cashOutDTO) {
        Account account = getAccount(cashOutDTO.getAccountId());
        var amountChange = -cashOutDTO.getAmount();
        var updatedAmount = account.getAmount() + amountChange;
        if (updatedAmount < 0)
            throw new RuntimeException("You cannot cash out this amount of money!");
        account.setAmount(updatedAmount);
        accountDAO.update(account);
        TransactionHistory transactionHistory = getTransaction(account, amountChange, TransactionHistoryType.CASH_OUT);
        transactionHistoryDAO.save(transactionHistory);
    }

    private AccountsAmountChangeDTO calculateCashTransfer(AccountManipulationDTO accountManipulationDTO, Account source, Account destination) {
        var sourceAmountChange = -accountManipulationDTO.getAmount();
        var sourceAmount = source.getAmount() + sourceAmountChange;
        var destinationAmountChange = accountManipulationDTO.getAmount();
        var destinationAmount = destination.getAmount() + destinationAmountChange;
        return new AccountsAmountChangeDTO(sourceAmountChange, sourceAmount, destinationAmountChange, destinationAmount);
    }

    private AccountsAmountChangeDTO calculateCurrencyExchange(AccountManipulationDTO accountManipulationDTO, Account source, Account destination) {
        var exchangeRate = CurrencyExchangeRates.getProperExchangeRate(source.getCurrency(), destination.getCurrency());
        var sourceAmountChange = -accountManipulationDTO.getAmount();
        var sourceAmount = source.getAmount() + sourceAmountChange;
        var destinationAmountChange = exchangeRate * accountManipulationDTO.getAmount();
        var destinationAmount = destination.getAmount() + destinationAmountChange;
        return new AccountsAmountChangeDTO(sourceAmountChange, sourceAmount, destinationAmountChange, destinationAmount);
    }

    private void updateAccountsAndCreateHistory(Account source, Account destination, AccountsAmountChangeDTO amountChangeDTO, TransactionHistoryType transactionHistoryType) {
        source.setAmount(amountChangeDTO.getSourceAmount());
        destination.setAmount(amountChangeDTO.getDestinationAmount());
        accountDAO.update(source);
        accountDAO.update(destination);
        TransactionHistory sourceTransactionHistory = getTransaction(source, amountChangeDTO.getSourceAmountChange(), transactionHistoryType);
        TransactionHistory destinationTransactionHistory = getTransaction(destination, amountChangeDTO.getDestinationAmountChange(), transactionHistoryType);
        transactionHistoryDAO.save(sourceTransactionHistory);
        transactionHistoryDAO.save(destinationTransactionHistory);
    }

    private TransactionHistory getTransaction(Account account, double amountChange, TransactionHistoryType transactionHistoryType) {
        return TransactionHistory.builder()
                .accountId(account.getId())
                .amount(amountChange)
                .type(transactionHistoryType)
                .date(new Date(System.currentTimeMillis()))
                .build();
    }

    public Account getAccount(int id) {
        return accountDAO.findById(id);
    }

}
