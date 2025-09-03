package com.bank.bankApp.repository;

import com.bank.bankApp.entity.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {

    @Query("SELECT b FROM Beneficiary b WHERE b.account.accountId = :accountId")
    Set<Beneficiary> findByAccountAccountId(@Param("accountId") Long accountId);

    boolean existsByBeneficiaryAccNo(Long beneficiaryAccNo);

    boolean existsByAccountAccountIdAndBeneficiaryAccNo(Long accountId, Long beneficiaryAccNo);

    Beneficiary findByBeneficiaryAccNo(Long beneficiaryAccNo);
}