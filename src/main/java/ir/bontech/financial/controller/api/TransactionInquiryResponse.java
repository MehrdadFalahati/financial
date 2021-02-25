package ir.bontech.financial.controller.api;

import ir.bontech.financial.domain.service.api.TransactionInquiryResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionInquiryResponse {
    private Long userId;
    private String firstName;
    private String lastName;

    public static TransactionInquiryResponse convert(TransactionInquiryResult result) {
        return new TransactionInquiryResponse(result.getUserId(), result.getFirstName(), result.getLastName());
    }
}
