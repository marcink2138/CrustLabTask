package dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountManipulationDTO {
    private int sourceAccountId;
    private int destinationAccountId;
    private double amount;
}
