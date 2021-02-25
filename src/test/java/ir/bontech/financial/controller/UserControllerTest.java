package ir.bontech.financial.controller;

import ir.bontech.financial.controller.api.AccountSaveRequest;
import ir.bontech.financial.controller.api.RegisterRequest;
import ir.bontech.financial.domain.service.UserService;
import ir.bontech.financial.domain.service.api.AccountSaveResult;
import ir.bontech.financial.domain.service.api.UserSaveResult;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(UserController.class)
class UserControllerTest extends AbstractRestControllerTest {

    @MockBean
    private UserService userService;

    @Test
    public void test_register_user_is_success() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .firstName("Mehrdad")
                .lastName("Falahati")
                .userName("m.falahati")
                .password("123456")
                .phoneNumber("09353507866")
                .build();

        UserSaveResult registerResult = UserSaveResult.builder().userId(1L).build();
        Mockito.when(userService.addUser(any())).thenReturn(registerResult);

        mvc.perform(post("/user/register")
                .content(toJson(registerRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(registerResult.getUserId()));
    }

    @Test
    public void test_add_accounts_for_user_is_success() throws Exception {
        AccountSaveRequest request = new AccountSaveRequest();
        request.setAccountInformation(List.of(new AccountSaveRequest.AccountInformation("123456", new BigDecimal(1000)),
                new AccountSaveRequest.AccountInformation("654321", new BigDecimal(2000))));

        AccountSaveResult accountSaveResult = new AccountSaveResult();
        accountSaveResult.setUserId(1L);
        accountSaveResult.setAccountNumbers(List.of("123456", "654321"));
        Mockito.when(userService.addAccounts(any(Long.class), any(AccountSaveRequest.class))).thenReturn(accountSaveResult);

        mvc.perform(post("/user/{0}/accounts", 1L)
                .content(toJson(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(accountSaveResult.getUserId()))
                .andExpect(jsonPath("$.accountNumbers", hasSize(accountSaveResult.getAccountNumbers().size())))
                .andExpect(jsonPath("$.accountNumbers[0]").value("123456"));
    }
}