package com.sau.bankingmanagementgradle.repositories;

import com.sau.bankingmanagementgradle.models.Account;
import com.sau.bankingmanagementgradle.models.Customer;
import com.sau.bankingmanagementgradle.models.Withdrawal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WithdrawalRepository extends JpaRepository<Withdrawal,Integer> {

    List<Withdrawal> findByCustomer(Customer customer);
    @Query("select w from Withdrawal w where w.account.branch LIKE CONCAT('%',:query,'%')")
    List<Withdrawal> searchName(String query);
    List<Withdrawal> findByAccount(Account account);
}
