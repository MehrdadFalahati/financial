package ir.bontech.financial.domain.service.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResult {
    private Long transactionId;
    private String withdrawAccountNumber;
    private String depositAccountNumber;
    private BigDecimal transferAmount;
    private Date transactionDate;
}
