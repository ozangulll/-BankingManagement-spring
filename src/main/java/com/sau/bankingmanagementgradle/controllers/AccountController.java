package com.sau.bankingmanagementgradle.controllers;

import com.sau.bankingmanagementgradle.models.Account;
import com.sau.bankingmanagementgradle.repositories.AccountRepository;
import com.sau.bankingmanagementgradle.servicesForUser.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class AccountController {
    private AccountRepository accountRepository;
    private UserService userService;

    @GetMapping("/")
    public String showHomePage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            model.addAttribute("isAuthenticated", false);
            model.addAttribute("username", "User");
        } else {
            String username = authentication.getName();
            model.addAttribute("isAuthenticated", true);
            model.addAttribute("username", username);
        }

        return "header";
    }
    @Autowired
    public AccountController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    @GetMapping("/accounts")
    public String listAccounts(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        List<Account> accounts = accountRepository.findAll();
        model.addAttribute("accounts", accounts);

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("admin"));

        model.addAttribute("isAdmin", isAdmin);

        // Debugging: Log isAdmin value
        System.out.println("isAdmin: " + isAdmin);

        return "/account/accounts-list";
    }
    @PreAuthorize("hasRole('ROLE_admin')")
    @GetMapping("/accounts/add")
    public String AddAccountForm(Model model){
        Account account=new Account();
        model.addAttribute("account",account);
        return "/account/create-account";
    }
    @PreAuthorize("hasRole('ROLE_admin')")
    @PostMapping("/accounts/add")
    public String AddAccount(@Valid @ModelAttribute("account") Account account, BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            return "/account/create-account";
        }
        if(accountRepository.existsById(account.getId())){
            model.addAttribute("errorMessage", "An account with this ID already exists!");
            return "/account/create-account";
        }
        accountRepository.save(account);
        return "redirect:/accounts";
    }
    @PreAuthorize("hasRole('ROLE_admin')")
    @GetMapping("/accounts/delete/{id}")
    public String DeleteAccountScreen(@PathVariable("id") int id, Model model){
        Optional<Account> account = accountRepository.findById(id);
        model.addAttribute("account", account);
        return "/account/delete-screen-account";
    }
    @PreAuthorize("hasRole('ROLE_admin')")
    @PostMapping("/accounts/delete/{id}")
    public String deleteAccount(@PathVariable("id") int id){
        accountRepository.deleteById(id);
        return "redirect:/accounts";
    }
    @GetMapping("/accounts/search")
    public String searchBranch(@RequestParam(value = "query") String query, Model model) {
        List<Account> accounts = accountRepository.searchBranch(query);
        model.addAttribute("accounts", accounts);
        return "/account/accounts-list";
    }
    @PreAuthorize("hasRole('ROLE_admin')")
    @GetMapping("accounts/update/{id}")
    public  String UpdateAccountForm( @PathVariable("id") int id, Model model){
        Optional<Account> account=accountRepository.findById(id);
        model.addAttribute("account",account);
        return "/account/update-account";
    }
    @PreAuthorize("hasRole('ROLE_admin')")
    @PostMapping("accounts/update/{id}")
    public  String UpdateAccount(@ModelAttribute("account")  @Valid Account account, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "/account/update-account";
        }
        accountRepository.save(account);
        return "redirect:/accounts";

    }
}
