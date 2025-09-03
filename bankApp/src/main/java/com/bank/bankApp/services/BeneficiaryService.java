package com.bank.bankApp.services;

import com.bank.bankApp.dtos.BeneficiaryRequest;
import com.bank.bankApp.dtos.BeneficiaryResponse;
import com.bank.bankApp.entity.Account;
import com.bank.bankApp.entity.Beneficiary;
import com.bank.bankApp.mapper.BeneficiaryMapper;
import com.bank.bankApp.repository.AccountRepository;
import com.bank.bankApp.repository.BeneficiaryRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BeneficiaryService {

    @Autowired
    private BeneficiaryRepository beneficiaryRepository;

    @Autowired
    private AccountRepository accountRepository;


     public BeneficiaryResponse addBeneficiary(BeneficiaryRequest request) {

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found with ID: " + request.getAccountId()));

        if (beneficiaryRepository.existsByAccountAccountIdAndBeneficiaryAccNo(request.getAccountId(), request.getBeneficiaryAccNo())) {
            throw new RuntimeException("Beneficiary with account number " + request.getBeneficiaryAccNo() + " already exists for this account");
        }

        Beneficiary beneficiary = BeneficiaryMapper.toEntity(request);
        beneficiary.setAccount(account);

        Beneficiary savedBeneficiary = beneficiaryRepository.save(beneficiary);

        return BeneficiaryMapper.toDTO(savedBeneficiary);
    }

    public BeneficiaryResponse updateBeneficiary(Long beneficiaryId, BeneficiaryRequest request) {

        Beneficiary existingBeneficiary = beneficiaryRepository.findById(beneficiaryId)
                .orElseThrow(() -> new RuntimeException("Beneficiary not found with ID: " + beneficiaryId));

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found with ID: " + request.getAccountId()));

        // Check if beneficiary account number already exists for another beneficiary
        Beneficiary existingWithSameAccNo = beneficiaryRepository.findByBeneficiaryAccNo(request.getBeneficiaryAccNo());
        if (existingWithSameAccNo != null && !existingWithSameAccNo.getBeneficiaryId().equals(beneficiaryId)) {
            throw new RuntimeException("Beneficiary with account number " + request.getBeneficiaryAccNo() + " already exists");
        }

        BeneficiaryMapper.updateEntity(existingBeneficiary, request);
        existingBeneficiary.setAccount(account);

        Beneficiary updatedBeneficiary = beneficiaryRepository.save(existingBeneficiary);

        return BeneficiaryMapper.toDTO(updatedBeneficiary);
    }


    public boolean deleteBeneficiary(Long beneficiaryId) {
        if (!beneficiaryRepository.existsById(beneficiaryId)) {
            throw new RuntimeException("Beneficiary not found with ID: " + beneficiaryId);
        }

        beneficiaryRepository.deleteById(beneficiaryId);
        return true;
    }


    @Transactional(readOnly = true)
    public BeneficiaryResponse findBeneficiaryById(Long beneficiaryId) {
        Beneficiary beneficiary = beneficiaryRepository.findById(beneficiaryId)
                .orElseThrow(() -> new RuntimeException("Beneficiary not found with ID: " + beneficiaryId));

        return BeneficiaryMapper.toDTO(beneficiary);
    }


    @Transactional(readOnly = true)
    public Set<BeneficiaryResponse> listAllBeneficiaries(Long accountId) {

        if (!accountRepository.existsById(accountId)) {
            throw new RuntimeException("Account not found with ID: " + accountId);
        }

        Set<Beneficiary> beneficiaries = beneficiaryRepository.findByAccountAccountId(accountId);

        return beneficiaries.stream()
                .map(BeneficiaryMapper::toDTO)
                .collect(Collectors.toSet());
    }
}