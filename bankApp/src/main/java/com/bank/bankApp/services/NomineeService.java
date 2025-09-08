package com.bank.bankApp.services;

import com.bank.bankApp.dtos.NomineeRequest;
import com.bank.bankApp.dtos.NomineeResponse;
import com.bank.bankApp.entity.Account;
import com.bank.bankApp.entity.Nominee;
import com.bank.bankApp.mapper.NomineeMapper;
import com.bank.bankApp.repository.AccountRepository;
import com.bank.bankApp.repository.NomineeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class NomineeService {

    @Autowired
    private NomineeRepository nomineeRepository;

    @Autowired
    private AccountRepository accountRepository;

    public NomineeResponse addNominee(NomineeRequest nomineeRequest) {
        if (nomineeRequest == null) {
            throw new IllegalArgumentException("Nominee cannot be null");
        }
        if (nomineeRequest.getName() == null || nomineeRequest.getGovtId() == null) {
            throw new IllegalArgumentException("Nominee name and government ID are required");
        }
        if (nomineeRequest.getRelation() == null) {
            throw new IllegalArgumentException("Relation is required");
        }
        if (nomineeRequest.getAccountId() == null) {
            throw new IllegalArgumentException("Account ID is required");
        }

        Account account = accountRepository.findById(nomineeRequest.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found with ID: " + nomineeRequest.getAccountId()));

        if (nomineeRepository.existsByAccountAccountIdAndGovtId(
                nomineeRequest.getAccountId(), nomineeRequest.getGovtId())) {
            throw new RuntimeException("Nominee with this Government ID already exists for this account");
        }

        Nominee nominee = NomineeMapper.toEntity(nomineeRequest);
        nominee.setAccount(account);

        Nominee savedNominee = nomineeRepository.save(nominee);
        return NomineeMapper.toDTO(savedNominee);
    }

    public NomineeResponse updateNominee(Integer nomineeId, NomineeRequest nomineeRequest) {
        if (nomineeRequest == null || nomineeId == null) {
            throw new IllegalArgumentException("Nominee and ID cannot be null");
        }

        Nominee existingNominee = nomineeRepository.findById(nomineeId)
                .orElseThrow(() -> new RuntimeException("Nominee not found with ID: " + nomineeId));

        // Check if govt ID is being changed and if it already exists
        if (!existingNominee.getGovtId().equals(nomineeRequest.getGovtId()) &&
                nomineeRepository.existsByGovtId(nomineeRequest.getGovtId())) {
            throw new RuntimeException("Nominee with this Government ID already exists");
        }

        NomineeMapper.updateEntity(existingNominee, nomineeRequest);

        Nominee updatedNominee = nomineeRepository.save(existingNominee);
        return NomineeMapper.toDTO(updatedNominee);
    }

    public NomineeResponse updateNominee(NomineeRequest nomineeRequest) {
        if (nomineeRequest == null) {
            throw new IllegalArgumentException("Nominee cannot be null");
        }

        Nominee existingNominee = nomineeRepository.findByGovtId(nomineeRequest.getGovtId())
                .orElseThrow(() -> new RuntimeException("Nominee not found with Government ID: " + nomineeRequest.getGovtId()));

        NomineeMapper.updateEntity(existingNominee, nomineeRequest);

        Nominee updatedNominee = nomineeRepository.save(existingNominee);
        return NomineeMapper.toDTO(updatedNominee);
    }

    public boolean deleteNominee(Long nomineeId) {
        if (nomineeId == null) {
            throw new IllegalArgumentException("Nominee ID cannot be null");
        }

        if (!nomineeRepository.existsById(nomineeId.intValue())) {
            throw new RuntimeException("Nominee not found with ID: " + nomineeId);
        }

        nomineeRepository.deleteById(nomineeId.intValue());
        return true;
    }

    @Transactional(readOnly = true)
    public NomineeResponse findNomineeById(Long nomineeId) {
        if (nomineeId == null) {
            throw new IllegalArgumentException("Nominee ID cannot be null");
        }

        Nominee nominee = nomineeRepository.findByIdWithAccount(nomineeId.intValue())
                .orElse(null);

        if (nominee == null) {
            throw new RuntimeException("Nominee not found with ID: " + nomineeId);
        }

        return NomineeMapper.toDTO(nominee);
    }

    @Transactional(readOnly = true)
    public Set<NomineeResponse> listAllNominees(Long accountId) {
        if (accountId == null) {
            throw new IllegalArgumentException("Account ID cannot be null");
        }

        return nomineeRepository.findByAccountAccountId(accountId)
                .stream()
                .map(NomineeMapper::toDTO)
                .collect(Collectors.toSet());
    }
}