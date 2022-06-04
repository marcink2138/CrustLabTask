package dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountsAmountChangeDTO {
    private double sourceAmountChange;
    private double sourceAmount;
    private double destinationAmountChange;
    private double destinationAmount;
}
