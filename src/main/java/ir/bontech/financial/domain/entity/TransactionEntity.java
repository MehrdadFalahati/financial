package ir.bontech.financial.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "FIN_TRANSACTION")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionEntity {

    @Id
    @Column(name = "transaction_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WITHDRAW_ACCOUNT_ID")
    private AccountEntity withdrawAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEPOSIT_ACCOUNT_ID")
    private AccountEntity depositAccount;

    private BigDecimal transactionAmount;

    @Temporal(TemporalType.TIMESTAMP)
    private Date transactionDate;

    @Version
    @Builder.Default
    private int version = 0;
}
