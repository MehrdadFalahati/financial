package ir.bontech.financial.controller;

import ir.bontech.financial.controller.api.*;
import ir.bontech.financial.domain.service.TransactionService;
import ir.bontech.financial.domain.service.api.TransactionInquiryResult;
import ir.bontech.financial.domain.service.api.TransactionReportResult;
import ir.bontech.financial.domain.service.api.TransactionResult;
import ir.bontech.financial.util.CsvWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static ir.bontech.financial.util.CsvWriter.RESULT;

@Slf4j
@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transfer-money")
    public ResponseEntity<TransactionResponse> transferMoney(@RequestBody TransactionRequest request) {
        final TransactionResult transactionResult = transactionService.transferMoney(request);
        return ResponseEntity.ok(TransactionResponse.convert(transactionResult));
    }

    @PostMapping("/report")
    public ResponseEntity<TransactionReportResponse> report(@RequestBody TransactionReportRequest request) {
        final TransactionReportResult transactionReportResult = transactionService.transactionReport(request);
        return ResponseEntity.ok(TransactionReportResponse.convert(transactionReportResult));
    }

    @GetMapping("{accountNumber}/inquiry")
    public ResponseEntity<TransactionInquiryResponse> inquiry(@PathVariable("accountNumber") String accountNumber) {
        final TransactionInquiryResult inquiry = transactionService.inquiry(accountNumber);
        return ResponseEntity.ok(TransactionInquiryResponse.convert(inquiry));
    }

    @GetMapping("{accountNumber}/report-file")
    public ResponseEntity<Resource> reportFile(@PathVariable("accountNumber") String accountNumber, HttpServletRequest request) {
        String fileName = RESULT + accountNumber + ".csv";
        transactionService.transactionReportFile(fileName, accountNumber);
        return getResourceResponseEntity(request, fileName, CsvWriter.getInputStream(fileName));
    }

    private ResponseEntity<Resource> getResourceResponseEntity(HttpServletRequest request, String fileName, InputStreamReader streamReader) {
        Resource resource = loadFileAsResource(fileName, streamReader);

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.info("Could not determine file type.");
        }

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    private Resource loadFileAsResource(String fileName, InputStreamReader streamReader) {
        try(InputStreamReader inputStreamReader = streamReader) {
            Path filePath = Paths.get(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            log.error("File not found " + fileName, ex);
            throw new RuntimeException("File not found " + fileName, ex);
        } catch (IOException ex) {
            log.error("IOException in write csv file", ex);
            throw new RuntimeException("IOException in write csv file", ex);
        }
    }
}
