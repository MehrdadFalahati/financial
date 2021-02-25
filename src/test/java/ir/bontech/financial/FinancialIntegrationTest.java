package ir.bontech.financial;

import ir.bontech.financial.controller.api.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FinancialIntegrationTest {

    @LocalServerPort
    int serverPort;

    @Test
    public void test_create_account_and_transfer_money__integration() {
        // save 2 user
        final ResponseEntity<RegisterResponse> registerUser1 = invokeRegisterUser(getRequest("Mehrdad", "Falahati", "m.falahati", "123"));
        checkInsertUser(registerUser1);
        final ResponseEntity<RegisterResponse> registerUser2 = invokeRegisterUser(getRequest("Mehdi", "Falahati", "m.falahati12", "321"));
        checkInsertUser(registerUser2);

        // add account for 2 user
        final ResponseEntity<AccountSaveResponse> accountSaveResponse1 = invokeAddAccount(registerUser1.getBody().getUserId(), createAccountSaveRequest(List.of(new AccountSaveRequest.AccountInformation("123456", new BigDecimal(1000000)))));
        checkAddAccountForUser(registerUser1, accountSaveResponse1);
        final ResponseEntity<AccountSaveResponse> accountSaveResponse2 = invokeAddAccount(registerUser2.getBody().getUserId(), createAccountSaveRequest(List.of(new AccountSaveRequest.AccountInformation("654321", new BigDecimal(2000000)))));
        checkAddAccountForUser(registerUser2, accountSaveResponse2);

        // inquiry from toAccount
        final ResponseEntity<TransactionInquiryResponse> transactionInquiryResponse = invokeInquiry("654321");
        assertNotNull(transactionInquiryResponse.getBody());
        assertEquals("Mehdi", transactionInquiryResponse.getBody().getFirstName());

        // transfer money from user1 to user2
        final ResponseEntity<TransactionResponse> transactionResponse = invokeTransferMoney(createTransactionRequest("123456", "654321", 1000));
        assertNotNull(transactionResponse.getBody());
        assertEquals("123456", transactionResponse.getBody().getWithdrawAccountNumber());
        assertEquals("654321", transactionResponse.getBody().getDepositAccountNumber());

        // report transaction
        final ResponseEntity<TransactionReportResponse> transactionReportResponse = invokeReport(createTransactionReport("123456"));
        assertNotNull(transactionReportResponse.getBody());
        assertEquals(1, transactionReportResponse.getBody().getTransactions().size());
    }

    private void checkAddAccountForUser(ResponseEntity<RegisterResponse> registerUser1, ResponseEntity<AccountSaveResponse> accountSaveResponse1) {
        assertNotNull(accountSaveResponse1.getBody());
        assertEquals(registerUser1.getBody().getUserId(), accountSaveResponse1.getBody().getUserId());
    }

    private void checkInsertUser(ResponseEntity<RegisterResponse> registerUser1) {
        assertNotNull(registerUser1.getBody());
        assertTrue(registerUser1.getBody().getUserId() > 0);
    }

    private RegisterRequest getRequest(String first, String last, String username, String pass) {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .firstName(first)
                .lastName(last)
                .userName(username)
                .password(pass)
                .build();
        return registerRequest;
    }

    private AccountSaveRequest createAccountSaveRequest(List<AccountSaveRequest.AccountInformation> accountInformation) {
        AccountSaveRequest accountSaveRequest = new AccountSaveRequest();
        accountSaveRequest.setAccountInformation(accountInformation);
        return accountSaveRequest;
    }

    private TransactionRequest createTransactionRequest(String fromAccount, String toAccount, int amount) {
        TransactionRequest request = new TransactionRequest();
        request.setFromAccountNumber(fromAccount);
        request.setToAccountNumber(toAccount);
        request.setAmount(new BigDecimal(amount));
        return request;
    }

    private TransactionReportRequest createTransactionReport(String accountNumber) {
        TransactionReportRequest request = new TransactionReportRequest();
        request.setAccountNumber(accountNumber);
        request.setFromDate("2021-02-25 00:00:00");
        request.setToDate(nowDate());
        return request;
    }

    private String nowDate() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis() * 10));
    }

    private ResponseEntity<RegisterResponse> invokeRegisterUser(RegisterRequest request) {
        TestRestTemplate restTemplate = new TestRestTemplate();
        return restTemplate.postForEntity(createUrl("/financial/user/register"), request, RegisterResponse.class);
    }

    private ResponseEntity<AccountSaveResponse> invokeAddAccount(Long userId, AccountSaveRequest request) {
        TestRestTemplate restTemplate = new TestRestTemplate();
        return restTemplate.postForEntity(createUrl("/financial/user/"+userId+"/accounts"), request, AccountSaveResponse.class);
    }

    private ResponseEntity<TransactionResponse> invokeTransferMoney(TransactionRequest request) {
        TestRestTemplate restTemplate = new TestRestTemplate();
        return restTemplate.postForEntity(createUrl("/financial/transaction/transfer-money"), request, TransactionResponse.class);
    }

    private ResponseEntity<TransactionInquiryResponse> invokeInquiry(String accountNumber) {
        TestRestTemplate restTemplate = new TestRestTemplate();
        return restTemplate.getForEntity(createUrl("/financial/transaction/"+accountNumber+"/inquiry"), TransactionInquiryResponse.class);
    }

    private ResponseEntity<TransactionReportResponse> invokeReport(TransactionReportRequest request) {
        TestRestTemplate restTemplate = new TestRestTemplate();
        return restTemplate.postForEntity(createUrl("/financial/transaction/report"), request, TransactionReportResponse.class);
    }

    private String createUrl(String path) {
        return "http://localhost:" + serverPort + path;
    }
}
