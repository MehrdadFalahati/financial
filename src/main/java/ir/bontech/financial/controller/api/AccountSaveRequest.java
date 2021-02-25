package ir.bontech.financial.controller.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
public class AccountSaveRequest {
    private List<AccountInformation> accountInformation;

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AccountInformation {
        private String AccountNumber;
        private BigDecimal currentBalance;
    }
}
