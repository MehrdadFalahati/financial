package ir.bontech.financial.controller.api;

import ir.bontech.financial.domain.service.api.TransactionResult;
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
public class TransactionResponse {
    private Long transactionId;
    private String withdrawAccountNumber;
    private String depositAccountNumber;
    private BigDecimal transferAmount;
    private Date transactionDate;

    public static TransactionResponse convert(TransactionResult result) {
        return TransactionResponse.builder()
                .transactionId(result.getTransactionId())
                .withdrawAccountNumber(result.getWithdrawAccountNumber())
                .depositAccountNumber(result.getDepositAccountNumber())
                .transferAmount(result.getTransferAmount())
                .transactionDate(result.getTransactionDate())
                .build();
    }
}
