package com.bank.bankApp.repository;

import com.bank.bankApp.entity.Nominee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NomineeRepository extends JpaRepository<Nominee, Integer> {

    List<Nominee> findByAccountAccountId(Long accountId);

    Optional<Nominee> findByGovtId(String govtId);
    boolean existsByAccountAccountIdAndGovtId(Long accountId, String govtId);


//    @Query("SELECT n FROM Nominee n WHERE n.name = :name AND n.account.accountId = :accountId")
//    List<Nominee> findByNameAndAccountId(@Param("name") String name, @Param("accountId") Long accountId);

    boolean existsByGovtId(String govtId);

    @Query("SELECT n FROM Nominee n JOIN FETCH n.account WHERE n.nomineeId = :id")
    Optional<Nominee> findByIdWithAccount(@Param("id") Integer id);
}