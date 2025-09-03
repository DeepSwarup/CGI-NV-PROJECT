package com.bank.bankApp.dtos;

import com.bank.bankApp.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeneficiaryRequest {

    @NotBlank(message = "Beneficiary name is required")
    private String beneficiaryName;

    @NotNull(message = "Beneficiary account number is required")
    private Long beneficiaryAccNo;

    @NotBlank(message = "IFSC code is required")
    private String ifsc;

    @NotNull(message = "Account type is required")
    private AccountType accountType;

    @NotNull(message = "Account ID is required")
    private Long accountId;
}