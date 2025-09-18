package com.bank.bankApp.controllers;

import com.bank.bankApp.dtos.NomineeRequest;
import com.bank.bankApp.dtos.NomineeResponse;
import com.bank.bankApp.services.NomineeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Set;

@RestController
@RequestMapping("/nominees")
@RequiredArgsConstructor
public class NomineeController {


    @Autowired
    private NomineeService nomineeService;

    @PostMapping
    public ResponseEntity<NomineeResponse> createNominee(@Valid @RequestBody NomineeRequest nomineeRequest) {
        NomineeResponse createdNominee = nomineeService.addNominee(nomineeRequest);
        return new ResponseEntity<>(createdNominee, HttpStatus.CREATED);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<Set<NomineeResponse>> getNomineesByAccountId(@PathVariable Long accountId) {
        Set<NomineeResponse> nominees = nomineeService.listAllNominees(accountId);

//        if (nominees.isEmpty()) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
        return new ResponseEntity<>(nominees, HttpStatus.OK);
    }

    @GetMapping("/by-nominee-id/{nomineeId}")
    public ResponseEntity<NomineeResponse> getNomineeById(@PathVariable Long nomineeId) {
        NomineeResponse nominee = nomineeService.findNomineeById(nomineeId);
        return new ResponseEntity<>(nominee, HttpStatus.OK);
    }

    @PutMapping("/{nomineeId}")
    public ResponseEntity<NomineeResponse> updateNominee(@PathVariable int nomineeId,
                                                         @Valid @RequestBody NomineeRequest nomineeRequest) {
        NomineeResponse updatedNominee = nomineeService.updateNominee(nomineeId, nomineeRequest);
        return new ResponseEntity<>(updatedNominee, HttpStatus.OK);
    }

    @DeleteMapping("/{nomineeId}")
    public ResponseEntity<String> deleteNominee(@PathVariable Long nomineeId) {
        nomineeService.deleteNominee(nomineeId);
        return new ResponseEntity<>("Nominee deleted successfully", HttpStatus.OK);
    }
}