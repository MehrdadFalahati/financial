package ir.bontech.financial.domain.service;

import ir.bontech.financial.controller.api.AccountSaveRequest;
import ir.bontech.financial.controller.api.RegisterRequest;
import ir.bontech.financial.domain.service.api.AccountSaveResult;
import ir.bontech.financial.domain.service.api.UserSaveResult;
import ir.bontech.financial.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @TestConfiguration
    @ComponentScan("ir.bontech.financial")
    public static class UserServiceTestConfiguration {
    }

    @Test
    public void test_addUser() {
        UserSaveResult userSaveResult = insertUser();
        assertTrue(userSaveResult.getUserId() > 0);
    }

    @Test
    public void test_addAccounts_to_user() {
        UserSaveResult userSaveResult = insertUser();
        AccountSaveRequest request = new AccountSaveRequest();
        request.setAccountInformation(List.of(new AccountSaveRequest.AccountInformation("123456", new BigDecimal(1000)),
                new AccountSaveRequest.AccountInformation("654321", new BigDecimal(2000))));
        AccountSaveResult result = userService.addAccounts(userSaveResult.getUserId(), request);
        assertEquals(2, result.getAccountNumbers().size());
        assertTrue(result.getAccountNumbers().contains("123456"));
    }

    @Test
    public void test_addAccounts_to_user_when_user_not_found() {
        AccountSaveRequest request = new AccountSaveRequest();
        request.setAccountInformation(List.of(new AccountSaveRequest.AccountInformation("123456", new BigDecimal(1000)),
                new AccountSaveRequest.AccountInformation("654321", new BigDecimal(2000))));
        assertThrows(NotFoundException.class, () -> userService.addAccounts(2L, request));
    }

    private UserSaveResult insertUser() {
        RegisterRequest registerRequest = getRequest();
        return userService.addUser(registerRequest);
    }

    private RegisterRequest getRequest() {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .firstName("Mehrdad")
                .lastName("Falahati")
                .userName("m.falahati")
                .password("123456")
                .phoneNumber("09353507866")
                .build();
        return registerRequest;
    }
}