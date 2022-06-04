package entities;

import enums.TransactionHistoryType;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class TransactionHistory {
    private int id;
    private double amount;
    private TransactionHistoryType type;
    private Date date;
    private int accountId;
}
