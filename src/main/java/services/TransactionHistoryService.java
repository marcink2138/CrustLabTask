package services;

import dtos.FilterParamsDTO;
import entities.TransactionHistory;
import interfaces.TransactionHistoryDAO;

import java.util.List;

public class TransactionHistoryService {
    private TransactionHistoryDAO transactionHistoryDAO;

    public TransactionHistoryService(TransactionHistoryDAO transactionHistoryDAO) {
        this.transactionHistoryDAO = transactionHistoryDAO;
    }

    public List<TransactionHistory> getTransactionHistory(FilterParamsDTO filterParamsDTO) {
        return transactionHistoryDAO.findAllByFilterParameters(filterParamsDTO);
    }

}
