package ir.bontech.financial.controller;

import ir.bontech.financial.controller.api.AccountSaveRequest;
import ir.bontech.financial.controller.api.AccountSaveResponse;
import ir.bontech.financial.controller.api.RegisterRequest;
import ir.bontech.financial.controller.api.RegisterResponse;
import ir.bontech.financial.domain.service.UserService;
import ir.bontech.financial.domain.service.api.AccountSaveResult;
import ir.bontech.financial.domain.service.api.UserSaveResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        UserSaveResult userSaveResult = userService.addUser(request);
        return ResponseEntity.ok(RegisterResponse.convert(userSaveResult));
    }

    @PostMapping("{userId}/accounts")
    public ResponseEntity<AccountSaveResponse> addAccount(@PathVariable("userId") Long userId, @RequestBody AccountSaveRequest request) {
        final AccountSaveResult accountSaveResult = userService.addAccounts(userId, request);
        return ResponseEntity.ok(AccountSaveResponse.convert(accountSaveResult));
    }
}
