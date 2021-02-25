package ir.bontech.financial.domain.service.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionReportResult {
    private List<TransactionResult> transactions;
}
