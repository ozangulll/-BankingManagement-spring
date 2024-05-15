package com.sau.bankingmanagementgradle.controllers;

import com.sau.bankingmanagementgradle.models.Account;
import com.sau.bankingmanagementgradle.models.Customer;
import com.sau.bankingmanagementgradle.models.Withdrawal;
import com.sau.bankingmanagementgradle.repositories.AccountRepository;
import com.sau.bankingmanagementgradle.repositories.CustomerRepository;
import com.sau.bankingmanagementgradle.repositories.WithdrawalRepository;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class WithdrawalController {

    WithdrawalRepository withdrawalRepository;
    AccountRepository accountRepository;
    CustomerRepository customerRepository;
    public WithdrawalController(WithdrawalRepository withdrawalRepository,AccountRepository accountRepository,CustomerRepository customerRepository){
        this.accountRepository=accountRepository;
        this.customerRepository=customerRepository;
        this.withdrawalRepository=withdrawalRepository;
    }
    //DisplayALLWithdrawals
    @GetMapping("withdrawals")
    public String getAllWithdrawals(Model model, Authentication authentication){
        List<Withdrawal> withdrawals=withdrawalRepository.findAll();
        model.addAttribute("withdrawals",withdrawals);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_admin"));

        model.addAttribute("isAdmin", isAdmin);
        return "/withdrawal/withdrawal-list";
    }
    @GetMapping("withdrawals/customer/{customerid}")
    public String getWithdrawalsForCustomerId(@PathVariable("customerid") int customerId, Model model) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            List<Withdrawal> withdrawals = withdrawalRepository.findByCustomer(customer);
            model.addAttribute("withdrawals", withdrawals);
            return "/withdrawal/withdrawal-list"; // Assuming there's a view named "withdrawal-list" to display withdrawals
        } else {
            return "customer-not-found"; // Example: return a view indicating customer not found
        }
    }
    @GetMapping("withdrawals/account/{accountId}")
    public String getWithdrawalsForAccountId(@PathVariable("accountId") int accountId, Model model) {
        Optional<Account> accountOptional = accountRepository.findById(accountId);

        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            List<Withdrawal> withdrawals = withdrawalRepository.findByAccount(account);
            model.addAttribute("withdrawals", withdrawals);
            return "/withdrawal/withdrawal-list"; // Assuming there's a view named "withdrawal-list" to display withdrawals
        } else {
            return "account-not-found"; // Example: return a view indicating account not found
        }
    }
    @PreAuthorize("hasRole('ROLE_admin')")
    @GetMapping("withdrawals/add")
    public String AddFormWithdrawal(Model model){
        List<Account> accounts=accountRepository.findAll();
        List<Customer> customers=customerRepository.findAll();
        Withdrawal withdrawal=new Withdrawal();
        model.addAttribute("withdrawal",withdrawal);
        model.addAttribute("customers",customers);
        model.addAttribute("accounts",accounts);
        return "/withdrawal/create-withdrawal";

    }
    @PreAuthorize("hasRole('ROLE_admin')")
    @PostMapping("withdrawals/add")
    public String addWithdrawal(@Valid @ModelAttribute("withdrawal") Withdrawal withdrawal, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {

            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.withdrawal", bindingResult);
            redirectAttributes.addFlashAttribute("withdrawal", withdrawal);
            return "redirect:/withdrawals/add";
        }

        Optional<Account> accountOptional = accountRepository.findById(withdrawal.getAccount().getId());
        if (!accountOptional.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Account not found");
            return "redirect:/withdrawals/add";
        }

        Account account = accountOptional.get();
        BigDecimal withdrawalAmount = withdrawal.getAmount();
        BigDecimal currentBalance = account.getBalance();
        if (currentBalance.compareTo(withdrawalAmount) < 0) {

            redirectAttributes.addFlashAttribute("errorMessage", "Insufficient balance");
            return "redirect:/withdrawals/add";
        }


        BigDecimal updatedBalance = currentBalance.subtract(withdrawalAmount);
        account.setBalance(updatedBalance);
        accountRepository.save(account);
        withdrawalRepository.save(withdrawal);
        return "redirect:/withdrawals";
    }

    @PreAuthorize("hasRole('ROLE_admin')")
    @GetMapping("/withdrawals/update/{id}")
    public String updateWithdrawalForm(@PathVariable("id") int id, Model model) {
        Optional<Withdrawal> optionalWithdrawal = withdrawalRepository.findById(id);
        if (optionalWithdrawal.isPresent()) {
            List<Account> accounts = accountRepository.findAll();
            List<Customer> customers = customerRepository.findAll();
            Withdrawal withdrawal = optionalWithdrawal.get();
            model.addAttribute("withdrawal", withdrawal);
            model.addAttribute("accounts", accounts);
            model.addAttribute("customers", customers);

            return "/withdrawal/update-withdrawal";
        } else {
            return "redirect:/withdrawals";
        }
    }
    @PreAuthorize("hasRole('ROLE_admin')")
    @PostMapping("/withdrawals/update/{id}")
    public String updateWithdrawal(@PathVariable("id") int id, @ModelAttribute("withdrawal") @Valid Withdrawal withdrawal, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.withdrawal", bindingResult);
            redirectAttributes.addFlashAttribute("withdrawal", withdrawal);
            return "redirect:/withdrawals/update/" + id;
        }

        Optional<Withdrawal> optionalWithdrawal = withdrawalRepository.findById(id);
        if (optionalWithdrawal.isPresent()) {
            Withdrawal existingWithdrawal = optionalWithdrawal.get();
            BigDecimal withdrawalAmount = withdrawal.getAmount();
            Account account = existingWithdrawal.getAccount();
            BigDecimal currentBalance = account.getBalance();
            BigDecimal oldWithdrawalAmount = existingWithdrawal.getAmount();
            if (withdrawalAmount.compareTo(oldWithdrawalAmount) > 0) {
                BigDecimal difference = withdrawalAmount.subtract(oldWithdrawalAmount);
                if (currentBalance.compareTo(difference) < 0) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Insufficient balance");
                    return "redirect:/withdrawals/update/" + id;
                }
            }
            BigDecimal updatedBalance = currentBalance.add(oldWithdrawalAmount).subtract(withdrawalAmount);
            account.setBalance(updatedBalance);
            existingWithdrawal.setAmount(withdrawalAmount);
            existingWithdrawal.setUpdatedDate(LocalDateTime.now()); // Set the updatedDate
            withdrawalRepository.save(existingWithdrawal);
            accountRepository.save(account);
            return "redirect:/withdrawals";
        } else {

            return "redirect:/withdrawals";
        }
    }

    @GetMapping("/withdrawals/search")
    public String searchName(@RequestParam(value = "query") String query, Model model) {
        List<Withdrawal> withdrawals = withdrawalRepository.searchName(query);
        model.addAttribute("withdrawals", withdrawals);
        return "/withdrawal/withdrawal-list";
    }
    @PreAuthorize("hasRole('ROLE_admin')")
    @GetMapping("withdrawals/delete/{id}")
    public String deleteWithdrawalForm(@PathVariable("id") int id, Model model){
        Optional<Withdrawal> withdrawal = withdrawalRepository.findById(id);
        model.addAttribute("withdrawal", withdrawal);
        return "/withdrawal/delete-screen-withdrawal";
    }
    @PreAuthorize("hasRole('ROLE_admin')")
    @PostMapping("withdrawals/delete/{id}")
    public String deleteWithdrawal(@PathVariable("id") int id){
        withdrawalRepository.deleteById(id);
        return "redirect:/withdrawals";
    }
}
