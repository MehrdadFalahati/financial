package ir.bontech.financial.controller;

import ir.bontech.financial.controller.api.TransactionReportRequest;
import ir.bontech.financial.controller.api.TransactionRequest;
import ir.bontech.financial.domain.service.TransactionService;
import ir.bontech.financial.domain.service.api.TransactionInquiryResult;
import ir.bontech.financial.domain.service.api.TransactionReportResult;
import ir.bontech.financial.domain.service.api.TransactionResult;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(TransactionController.class)
class TransactionControllerTest extends AbstractRestControllerTest {

    @MockBean
    private TransactionService service;

    @Test
    public void test_transfer_money_is_success() throws Exception {
        TransactionRequest request = new TransactionRequest();
        request.setFromAccountNumber("123456");
        request.setFromAccountNumber("654321");
        request.setAmount(new BigDecimal(10000));

        TransactionResult result = TransactionResult.builder().transactionId(1L).build();
        Mockito.when(service.transferMoney(any(TransactionRequest.class))).thenReturn(result);

        mvc.perform(post("/transaction/transfer-money")
                .content(toJson(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.transactionId").value(result.getTransactionId()));
    }

    @Test
    public void test_report_is_success() throws Exception {
        TransactionReportRequest request = new TransactionReportRequest();
        request.setAccountNumber("123456");
        request.setFromDate("2021-02-25 00:00:00");
        request.setToDate("2021-02-28 00:00:00");

        TransactionReportResult result = new TransactionReportResult(List.of(new TransactionResult(), new TransactionResult()));
        Mockito.when(service.transactionReport(any(TransactionReportRequest.class))).thenReturn(result);

        mvc.perform(post("/transaction/report")
                .content(toJson(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.transactions", hasSize(result.getTransactions().size())));
    }

    @Test
    public void test_inquiry_is_success() throws Exception {

        TransactionInquiryResult result = new TransactionInquiryResult(1L, "M", "F");
        Mockito.when(service.inquiry(any(String.class))).thenReturn(result);

        mvc.perform(get("/transaction/{0}/inquiry", "123456")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(result.getUserId()));
    }
}