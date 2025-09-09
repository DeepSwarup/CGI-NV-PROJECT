package com.bank.bankApp.entity;

import com.bank.bankApp.enums.GovtIdType;
import com.bank.bankApp.enums.Relation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "nominees")
public class Nominee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nominee_id")
    private Integer nomineeId;

    @Column(nullable = false)
    private String name;

    @Column(name = "govt_id", nullable = false)
    private String govtId;

    @Enumerated(EnumType.STRING)
    @Column(name = "govt_id_type", nullable = false)
    private GovtIdType govtIdType;

    @Column(name = "phone_no", nullable = false)
    private String phoneNo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Relation relation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    public Nominee(String name, String govtId, GovtIdType govtIdType,
                   String phoneNo, Relation relation, Account account) {
        this.name = name;
        this.govtId = govtId;
        this.govtIdType = govtIdType;
        this.phoneNo = phoneNo;
        this.relation = relation;
        this.account = account;
    }
}