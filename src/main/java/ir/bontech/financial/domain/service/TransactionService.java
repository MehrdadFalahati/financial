package ir.bontech.financial.domain.service;

import ir.bontech.financial.controller.api.TransactionReportRequest;
import ir.bontech.financial.controller.api.TransactionRequest;
import ir.bontech.financial.domain.entity.AccountEntity;
import ir.bontech.financial.domain.entity.TransactionEntity;
import ir.bontech.financial.domain.entity.UserEntity;
import ir.bontech.financial.domain.service.api.TransactionInquiryResult;
import ir.bontech.financial.domain.service.api.TransactionReportResult;
import ir.bontech.financial.domain.service.api.TransactionResult;
import ir.bontech.financial.exception.NotFoundException;
import ir.bontech.financial.exception.TransferMoneyException;
import ir.bontech.financial.exception.UploadFileException;
import ir.bontech.financial.repository.AccountRepository;
import ir.bontech.financial.repository.TransactionRepository;
import ir.bontech.financial.repository.UserRepository;
import ir.bontech.financial.util.CsvWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Transactional
    public TransactionResult transferMoney(TransactionRequest request) {
        final AccountEntity fromAccount = getByAccountNumber(request.getFromAccountNumber());
        if (!checkAccountHasCredit(request, fromAccount)) {
            log.error("this {} dont have enough money", fromAccount);
            throw new TransferMoneyException("your account dont have enough money");
        }
        final AccountEntity toAccount = getByAccountNumber(request.getToAccountNumber());
        AccountEntity withdrawAccount = withdraw(request, fromAccount);
        AccountEntity depositAccount = deposit(request, toAccount);

        TransactionEntity transaction = addTransaction(request, withdrawAccount, depositAccount);
        log.debug("transfer money between {} and {} in date {}", transaction.getWithdrawAccount().getAccountNumber()
                , transaction.getDepositAccount().getAccountNumber(), transaction.getTransactionDate());
        return getTransactionResult(transaction);
    }

    @Transactional(readOnly = true)
    public TransactionReportResult transactionReport(TransactionReportRequest request) {
        final List<TransactionEntity> transactions = transactionRepository.findAllByWithdrawAccount_AccountNumberAndTransactionDateBetween(request.getAccountNumber(),
                request.parseFromDate(), request.parseToDate());
        return getReport(transactions);
    }

    @Transactional(readOnly = true)
    public TransactionInquiryResult inquiry(String accountNumber) {
        final AccountEntity account = getByAccountNumber(accountNumber);
        final UserEntity user = userRepository.findByAccountsId(account.getId());
        log.debug("inquiry {}", account);
        return new TransactionInquiryResult(user.getId(), user.getFirstName(), user.getLastName());
    }

    @Transactional(readOnly = true)
    public void transactionReportFile(String fileName, String accountNumber) {
        final List<TransactionEntity> transactions = transactionRepository.findAllByWithdrawAccount_AccountNumber(accountNumber);
        try {
            new CsvWriter(fileName).createCsvFile(transactions);
        } catch (IOException e) {
            log.error("I/O exception to create csv file={}", fileName);
            throw new UploadFileException("error in export file");
        }
    }

    private TransactionReportResult getReport(List<TransactionEntity> transactions) {
        final List<TransactionResult> transactionResults = transactions.stream().map(this::getTransactionResult).collect(Collectors.toList());
        return new TransactionReportResult(transactionResults);
    }

    private AccountEntity getByAccountNumber(String fromAccountNumber) {
        return accountRepository.findByAccountNumber(fromAccountNumber).orElseThrow(() -> new NotFoundException("can not find account=" + fromAccountNumber));
    }

    private TransactionResult getTransactionResult(TransactionEntity transaction) {
        return TransactionResult.builder()
                .transactionId(transaction.getId())
                .withdrawAccountNumber(transaction.getWithdrawAccount().getAccountNumber())
                .depositAccountNumber(transaction.getDepositAccount().getAccountNumber())
                .transferAmount(transaction.getTransactionAmount())
                .transactionDate(transaction.getTransactionDate())
                .build();
    }

    private TransactionEntity addTransaction(TransactionRequest request, AccountEntity withdrawAccount, AccountEntity depositAccount) {
        final TransactionEntity transaction = TransactionEntity.builder()
                .withdrawAccount(withdrawAccount)
                .depositAccount(depositAccount)
                .transactionAmount(request.getAmount())
                .transactionDate(new Date())
                .build();
        return transactionRepository.save(transaction);
    }

    private AccountEntity deposit(TransactionRequest request, AccountEntity toAccount) {
        toAccount.setCurrentBalance(toAccount.getCurrentBalance().add(request.getAmount()));
        final AccountEntity result = accountRepository.save(toAccount);
        log.debug("deposit account update , {}", result);
        return result;
    }

    private AccountEntity withdraw(TransactionRequest request, AccountEntity fromAccount) {
        fromAccount.setCurrentBalance(fromAccount.getCurrentBalance().subtract(request.getAmount()));
        final AccountEntity result = accountRepository.save(fromAccount);
        log.debug("withdraw account update , {}", result);
        return result;
    }

    private boolean checkAccountHasCredit(TransactionRequest request, AccountEntity fromAccount) {
        return fromAccount.getCurrentBalance().compareTo(BigDecimal.ONE) >= 1 && fromAccount.getCurrentBalance().compareTo(request.getAmount()) >= 1;
    }
}
