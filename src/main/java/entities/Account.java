package entities;

import enums.Currency;
import lombok.Data;

@Data
public class Account {
    private int id;
    private Currency currency;
    private double amount;
    private int userId;
}
