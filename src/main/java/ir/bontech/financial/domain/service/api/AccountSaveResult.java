package ir.bontech.financial.domain.service.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountSaveResult {
    private Long userId;
    private List<String> accountNumbers;
}
