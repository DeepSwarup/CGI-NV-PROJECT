package com.bank.bankApp.entity;

import com.bank.bankApp.enums.AccountType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "beneficiaries",
        uniqueConstraints = @UniqueConstraint(columnNames = {"account_id", "beneficiary_acc_no"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Beneficiary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long beneficiaryId;

    @Column(nullable = false)
    private String beneficiaryName;

    @Column(nullable = false)
    private Long beneficiaryAccNo;

    @Column(nullable = false)
    private String ifsc;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType accountType;

    // Many-to-One relationship with Account
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;


    public Beneficiary(String beneficiaryName, Long beneficiaryAccNo, String ifsc, AccountType accountType, Account account) {
        this.beneficiaryName = beneficiaryName;
        this.beneficiaryAccNo = beneficiaryAccNo;
        this.ifsc = ifsc;
        this.accountType = accountType;
        this.account = account;
    }

}
