package dtos;

import entities.TransactionHistory;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AccountBalanceDTO {
    private double amount;
    private List<TransactionHistory> transactionHistoryList;
}
