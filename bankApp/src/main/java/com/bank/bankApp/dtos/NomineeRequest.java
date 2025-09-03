package com.bank.bankApp.dtos;

import com.bank.bankApp.enums.GovtIdType;
import com.bank.bankApp.enums.Relation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NomineeRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Government ID is required")
    private String govtId;

    @NotNull(message = "Government ID type is required")
    private GovtIdType govtIdType;

    private String phoneNo;

    @NotNull(message = "Relation is required")
    private Relation relation;

    @NotNull(message = "Account ID is required")
    private Long accountId;
}
