package ir.bontech.financial.controller.api;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransactionRequest {
    private String fromAccountNumber;
    private String toAccountNumber;
    private BigDecimal amount;
}
