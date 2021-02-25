package ir.bontech.financial.repository;

import ir.bontech.financial.domain.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    List<TransactionEntity> findAllByWithdrawAccount_AccountNumber(String accountNumber);
    List<TransactionEntity> findAllByWithdrawAccount_AccountNumberAndTransactionDateBetween(String withdrawAccount, Date fromDate, Date toDate);
}
