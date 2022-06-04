package dtos;

import enums.Currency;
import enums.TransactionHistoryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterParamsDTO {
    private Integer userId;
    private Integer accountId;
    private Currency currency;
    private TransactionHistoryType transactionHistoryType;
    private Date fromDate;
    private Date toDate;
}
