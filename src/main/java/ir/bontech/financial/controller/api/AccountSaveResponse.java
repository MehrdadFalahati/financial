package ir.bontech.financial.controller.api;

import ir.bontech.financial.domain.service.api.AccountSaveResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountSaveResponse {
    private Long userId;
    private List<String> accountNumbers;

    public static AccountSaveResponse convert(AccountSaveResult result) {
        return new AccountSaveResponse(result.getUserId(), result.getAccountNumbers());
    }
}
