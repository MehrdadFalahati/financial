package ir.bontech.financial.util;

import com.opencsv.CSVWriter;
import ir.bontech.financial.domain.entity.TransactionEntity;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Slf4j
public class CsvWriter {
    private static final String SLASH_SEPARATOR = "\"";
    private static final String TWO_SLASH_SEPARATOR = "\"\"";
    public static final String RESULT = "transactionReport_";

    private Writer writer;
    private String path;

    public CsvWriter(String path) throws IOException {
        this.path = path;
        this.writer = new FileWriter(path);
        writeLine(List.of("TRANSACTION_ID","WITHDRAW_ACCOUNT_ID","TRANSACTION_AMOUNT","DEPOSIT_ACCOUNT_ID","TRANSACTION_DATE"));
    }

    public InputStreamReader createCsvFile(List<TransactionEntity> transactions) throws IOException {
        for(TransactionEntity transaction : transactions)
            writeLine(List.of(String.valueOf(transaction.getId()),
                    transaction.getWithdrawAccount().getAccountNumber(),
                    transaction.getTransactionAmount().toString(),
                    transaction.getDepositAccount().getAccountNumber(),
                    transaction.getTransactionDate().toString()));
        writer.flush();
        writer.close();
        InputStream inputStream = new FileInputStream(path);
        return new InputStreamReader(inputStream, StandardCharsets.UTF_8);
    }

    public static InputStreamReader getInputStream(String path) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            log.warn("can not find file {}", path);
        }
        return new InputStreamReader(inputStream, StandardCharsets.UTF_8);
    }

    void writeLine(List<String> records) throws IOException {
        boolean firstColumn = true;
        StringBuilder column = new StringBuilder();

        for (String record : records) {
            if (!firstColumn)
                column.append(CSVWriter.DEFAULT_SEPARATOR);

            column.append(followCVSFormat(record));
            firstColumn = false;
        }

        column.append("\n");
        writer.append(column.toString());
    }

    private String followCVSFormat(String column) {
        if (column.contains(SLASH_SEPARATOR)) {
            column = column.replace(SLASH_SEPARATOR, TWO_SLASH_SEPARATOR);
        }
        return column;
    }
}
