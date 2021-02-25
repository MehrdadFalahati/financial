package ir.bontech.financial.domain.service;

import ir.bontech.financial.controller.api.AccountSaveRequest;
import ir.bontech.financial.controller.api.RegisterRequest;
import ir.bontech.financial.controller.api.TransactionReportRequest;
import ir.bontech.financial.controller.api.TransactionRequest;
import ir.bontech.financial.domain.entity.TransactionEntity;
import ir.bontech.financial.domain.service.api.TransactionInquiryResult;
import ir.bontech.financial.domain.service.api.TransactionReportResult;
import ir.bontech.financial.domain.service.api.TransactionResult;
import ir.bontech.financial.domain.service.api.UserSaveResult;
import ir.bontech.financial.exception.NotFoundException;
import ir.bontech.financial.exception.TransferMoneyException;
import ir.bontech.financial.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private UserService userService;
    @Autowired
    private TransactionRepository repository;

    @TestConfiguration
    @ComponentScan("ir.bontech.financial")
    public static class TransactionServiceTestConfiguration {
    }

    @Test
    public void test_transferMoney_when_dosent_find_account() {
        TransactionRequest request = new TransactionRequest();
        request.setFromAccountNumber("123456");
        request.setToAccountNumber("654321");
        request.setAmount(new BigDecimal(10000));

        assertThrows(NotFoundException.class, () -> transactionService.transferMoney(request));
    }

    @Test
    public void test_transferMoney_is_success() {
        createAccount();
        TransactionResult transactionResult = getTransactionResult(new BigDecimal(10000));
        assertNotNull(transactionResult);
        assertTrue(transactionResult.getTransactionId() > 0);
        assertEquals("123456", transactionResult.getWithdrawAccountNumber());
        assertEquals("654321", transactionResult.getDepositAccountNumber());
        assertEquals(new BigDecimal(10000), transactionResult.getTransferAmount());
        assertNotNull(transactionResult.getTransactionDate());
    }

    private TransactionResult getTransactionResult(BigDecimal amount) {


        TransactionRequest request = new TransactionRequest();
        request.setFromAccountNumber("123456");
        request.setToAccountNumber("654321");
        request.setAmount(amount);

        TransactionResult transactionResult = transactionService.transferMoney(request);
        return transactionResult;
    }

    @Test
    public void test_transferMoney_when_from_account_dosent_have_enough_money() {
        createPoorAccount();

        TransactionRequest request = new TransactionRequest();
        request.setFromAccountNumber("123456");
        request.setToAccountNumber("654321");
        request.setAmount(new BigDecimal(10000));

        assertThrows(TransferMoneyException.class, () -> transactionService.transferMoney(request));
    }

    @Test
    public void test_reportTransaction() {
        createAccount();
        getTransactionResult(new BigDecimal(1000));
        getTransactionResult(new BigDecimal(2000));
        getTransactionResult(new BigDecimal(3000));

        TransactionReportRequest request = new TransactionReportRequest();
        request.setAccountNumber("123456");
        request.setFromDate("2021-02-25 00:00:00");
        request.setToDate(nowDate());

        final TransactionReportResult transactionReportResult = transactionService.transactionReport(request);
        assertNotNull(transactionReportResult);
        assertEquals(3, transactionReportResult.getTransactions().size());
    }

    @Test
    public void test_reportTransactionFile() {
        createAccount();
        getTransactionResult(new BigDecimal(1000));
        getTransactionResult(new BigDecimal(2000));
        getTransactionResult(new BigDecimal(3000));

        transactionService.transactionReportFile("result.csv", "123456");
        final List<String> reports = readFile("result.csv");
        assertNotNull(reports);
        assertEquals(4, reports.size());
    }

    @Test
    public void test_inquiry() {
        createAccount();

        final TransactionInquiryResult inquiryAccount = transactionService.inquiry("123456");
        assertNotNull(inquiryAccount);
        assertEquals("Mehrdad", inquiryAccount.getFirstName());
    }

    private void createPoorAccount() {
        RegisterRequest user1 = getRequest("Mehrdad", "Falahati", "m.falahati", "123");
        final UserSaveResult userSaveResult1 = userService.addUser(user1);
        AccountSaveRequest request1 = new AccountSaveRequest();
        request1.setAccountInformation(List.of(new AccountSaveRequest.AccountInformation("123456", new BigDecimal(1000))));
        userService.addAccounts(userSaveResult1.getUserId(), request1);
    }

    private void createAccount() {
        RegisterRequest user1 = getRequest("Mehrdad", "Falahati", "m.falahati", "123");
        RegisterRequest user2 = getRequest("Mehdi", "Falahati", "m.falahati12", "321");
        final UserSaveResult userSaveResult1 = userService.addUser(user1);
        final UserSaveResult userSaveResult2 = userService.addUser(user2);
        AccountSaveRequest request1 = new AccountSaveRequest();
        AccountSaveRequest request2 = new AccountSaveRequest();
        request1.setAccountInformation(List.of(new AccountSaveRequest.AccountInformation("123456", new BigDecimal(1000000))));
        request2.setAccountInformation(List.of(new AccountSaveRequest.AccountInformation("654321", new BigDecimal(2000000))));
        userService.addAccounts(userSaveResult1.getUserId(), request1);
        userService.addAccounts(userSaveResult2.getUserId(), request2);
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

    private String nowDate() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis() * 10));
    }

    public static List<String> readFile(String fileName) {
        List<String> results = new ArrayList<>();
        try {
            results = Files.readAllLines(Paths.get(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }
}