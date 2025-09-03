package com.bank.bankApp.controllers;

import com.bank.bankApp.dtos.BeneficiaryRequest;
import com.bank.bankApp.dtos.BeneficiaryResponse;
import com.bank.bankApp.services.BeneficiaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/beneficiaries")
@RequiredArgsConstructor
public class BeneficiaryController {


    @Autowired
    private BeneficiaryService beneficiaryService;


    @PostMapping
    public ResponseEntity<BeneficiaryResponse> addBeneficiary(@Valid @RequestBody BeneficiaryRequest request) {
        BeneficiaryResponse response = beneficiaryService.addBeneficiary(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @PutMapping("/{beneficiaryId}")
    public ResponseEntity<BeneficiaryResponse> updateBeneficiary(
            @PathVariable Long beneficiaryId,
            @Valid @RequestBody BeneficiaryRequest request) {
        BeneficiaryResponse response = beneficiaryService.updateBeneficiary(beneficiaryId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @DeleteMapping("/{beneficiaryId}")
    public ResponseEntity<String> deleteBeneficiary(@PathVariable Long beneficiaryId) {
        boolean deleted = beneficiaryService.deleteBeneficiary(beneficiaryId);
        if (deleted) {
            return new ResponseEntity<>("Beneficiary deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failed to delete beneficiary", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/{beneficiaryId}")
    public ResponseEntity<BeneficiaryResponse> findBeneficiaryById(@PathVariable Long beneficiaryId) {
        BeneficiaryResponse response = beneficiaryService.findBeneficiaryById(beneficiaryId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("/account/{accountId}")
    public ResponseEntity<Set<BeneficiaryResponse>> listAllBeneficiaries(@PathVariable Long accountId) {
        Set<BeneficiaryResponse> beneficiaries = beneficiaryService.listAllBeneficiaries(accountId);
        return new ResponseEntity<>(beneficiaries, HttpStatus.OK);
    }


    @GetMapping
    public ResponseEntity<Set<BeneficiaryResponse>> listBeneficiariesByAccountId(@RequestParam Long accountId) {
        Set<BeneficiaryResponse> beneficiaries = beneficiaryService.listAllBeneficiaries(accountId);
        return new ResponseEntity<>(beneficiaries, HttpStatus.OK);
    }
}