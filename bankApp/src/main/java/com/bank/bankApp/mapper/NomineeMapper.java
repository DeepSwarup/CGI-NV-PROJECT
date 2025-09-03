package com.bank.bankApp.mapper;

import com.bank.bankApp.dtos.NomineeRequest;
import com.bank.bankApp.dtos.NomineeResponse;
import com.bank.bankApp.entity.Nominee;

public class NomineeMapper {

    public static NomineeResponse toDTO(Nominee nominee) {
        return NomineeResponse.builder()
                .nomineeId(nominee.getNomineeId())
                .name(nominee.getName())
                .govtId(nominee.getGovtId())
                .govtIdType(nominee.getGovtIdType())
                .phoneNo(nominee.getPhoneNo())
                .relation(nominee.getRelation())
                .accountId(nominee.getAccount().getAccountId())
                .build();
    }

    public static Nominee toEntity(NomineeRequest dto) {
        Nominee nominee = new Nominee();
        nominee.setName(dto.getName());
        nominee.setGovtId(dto.getGovtId());
        nominee.setGovtIdType(dto.getGovtIdType());
        nominee.setPhoneNo(dto.getPhoneNo());
        nominee.setRelation(dto.getRelation());
        return nominee;
    }

    public static void updateEntity(Nominee nominee, NomineeRequest dto) {
        nominee.setName(dto.getName());
        nominee.setGovtId(dto.getGovtId());
        nominee.setGovtIdType(dto.getGovtIdType());
        nominee.setPhoneNo(dto.getPhoneNo());
        nominee.setRelation(dto.getRelation());
    }
}