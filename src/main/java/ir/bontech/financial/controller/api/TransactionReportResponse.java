package ir.bontech.financial.controller.api;

import ir.bontech.financial.domain.service.api.TransactionReportResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionReportResponse {
    private List<TransactionResponse> transactions;

    public static TransactionReportResponse convert(TransactionReportResult result) {
        final List<TransactionResponse> transactions = result.getTransactions().stream().map(TransactionResponse::convert).collect(Collectors.toList());
        return new TransactionReportResponse(transactions);
    }
}
