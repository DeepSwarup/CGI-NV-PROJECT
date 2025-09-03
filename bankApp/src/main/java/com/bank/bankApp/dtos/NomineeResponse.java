package com.bank.bankApp.dtos;

import com.bank.bankApp.enums.GovtIdType;
import com.bank.bankApp.enums.Relation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NomineeResponse {

    private Integer nomineeId;
    private String name;
    private String govtId;
    private GovtIdType govtIdType;
    private String phoneNo;
    private Relation relation;
    private Long accountId;
}
