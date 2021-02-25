package ir.bontech.financial.domain.service.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionInquiryResult {
    private Long userId;
    private String firstName;
    private String lastName;
}
