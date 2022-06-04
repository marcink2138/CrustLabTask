package interfaces;

import dtos.FilterParamsDTO;
import entities.TransactionHistory;

import java.util.List;

public interface TransactionHistoryDAO {
    void save(TransactionHistory transactionHistory);
    List<TransactionHistory> findAllByFilterParameters(FilterParamsDTO filterParamsDTO);
}
