package com.bank.bankApp.mapper;

import com.bank.bankApp.dtos.BeneficiaryRequest;
import com.bank.bankApp.dtos.BeneficiaryResponse;
import com.bank.bankApp.entity.Beneficiary;

public class BeneficiaryMapper {

    public static BeneficiaryResponse toDTO(Beneficiary beneficiary) {
        return BeneficiaryResponse.builder()
                .beneficiaryId(beneficiary.getBeneficiaryId())
                .beneficiaryName(beneficiary.getBeneficiaryName())
                .beneficiaryAccNo(beneficiary.getBeneficiaryAccNo())
                .ifsc(beneficiary.getIfsc())
                .accountType(beneficiary.getAccountType())
                .accountId(beneficiary.getAccount().getAccountId())
                .build();
    }

    public static Beneficiary toEntity(BeneficiaryRequest dto) {
        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setBeneficiaryName(dto.getBeneficiaryName());
        beneficiary.setBeneficiaryAccNo(dto.getBeneficiaryAccNo());
        beneficiary.setIfsc(dto.getIfsc());
        beneficiary.setAccountType(dto.getAccountType());
        return beneficiary;
    }

    public static void updateEntity(Beneficiary beneficiary, BeneficiaryRequest dto) {
        beneficiary.setBeneficiaryName(dto.getBeneficiaryName());
        beneficiary.setBeneficiaryAccNo(dto.getBeneficiaryAccNo());
        beneficiary.setIfsc(dto.getIfsc());
        beneficiary.setAccountType(dto.getAccountType());
    }
}
