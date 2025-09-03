package com.bank.bankApp.dtos;

import com.bank.bankApp.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeneficiaryResponse {

    private Long beneficiaryId;
    private String beneficiaryName;
    private Long beneficiaryAccNo;
    private String ifsc;
    private AccountType accountType;
    private Long accountId;
}