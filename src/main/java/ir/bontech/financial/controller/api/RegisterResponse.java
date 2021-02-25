package ir.bontech.financial.controller.api;

import ir.bontech.financial.domain.service.api.UserSaveResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse {
    private Long userId;

    public static RegisterResponse convert(UserSaveResult result) {
        return RegisterResponse.builder()
                .userId(result.getUserId())
                .build();
    }
}
