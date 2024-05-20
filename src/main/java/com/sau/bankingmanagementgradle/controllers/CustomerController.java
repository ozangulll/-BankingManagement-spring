package com.sau.bankingmanagementgradle.controllers;

import com.sau.bankingmanagementgradle.models.Customer;
import com.sau.bankingmanagementgradle.repositories.CustomerRepository;
import com.sau.bankingmanagementgradle.servicesForUser.impl.FileUploadService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Controller
public class CustomerController {
    private final CustomerRepository customerRepository;
    private final FileUploadService fileUploadService;

    @Autowired
    public CustomerController(CustomerRepository customerRepository, FileUploadService fileUploadService) {
        this.customerRepository = customerRepository;
        this.fileUploadService = fileUploadService;
    }

    @GetMapping("/customers")
    public String listCustomers(Model model, Authentication authentication) {
        List<Customer> customers = customerRepository.findAll();
        model.addAttribute("customers", customers);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_admin"));
        model.addAttribute("isAdmin", isAdmin);
        return "/customer/customers-list";
    }

    @PreAuthorize("hasRole('ROLE_admin')")
    @GetMapping("customers/add")
    public String addCustomerForm(Model model) {
        Customer customer = new Customer();
        model.addAttribute("customer", customer);
        return "/customer/create-customer";
    }

    @PreAuthorize("hasRole('ROLE_admin')")
    @PostMapping("customers/add")
    public String addCustomer(@Valid @ModelAttribute("customer") Customer customer, @RequestParam("file") MultipartFile file, BindingResult bindingResult, Model model) throws IOException {
        if (bindingResult.hasErrors()) {
            return "/customer/create-customer";
        }

        String fileName = file.getOriginalFilename();
        customer.setPhotoUrl(fileName);
        String uploadDir = "src/main/resources/static/images/";
        Path uploadPath = Paths.get(uploadDir, fileName);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, uploadPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            System.out.println("File saving error! " + ex.toString());
            model.addAttribute("errorMessage", "File saving error!");
            return "/customer/create-customer";
        }

        if (customerRepository.existsById(customer.getId())) {
            model.addAttribute("errorMessage", "A customer with this ID already exists!");
            return "/customer/create-customer";
        }

        customerRepository.save(customer);
        return "redirect:/customers";
    }

    @PreAuthorize("hasRole('ROLE_admin')")
    @GetMapping("customers/update/{id}")
    public String updateCustomerForm(@PathVariable("id") int id, Model model) {
        Optional<Customer> customer = customerRepository.findById(id);
        if (customer.isPresent()) {
            model.addAttribute("customer", customer.get());
            return "/customer/update-customer";
        } else {
            return "redirect:/customers";
        }
    }

    @PreAuthorize("hasRole('ROLE_admin')")
    @PostMapping("customers/update/{id}")
    public String updateCustomer(@PathVariable("id") int id,
                                 @ModelAttribute("customer") @Valid Customer customer,
                                 @RequestParam("file") MultipartFile file,
                                 BindingResult bindingResult,
                                 Model model) throws IOException {
        if (bindingResult.hasErrors()) {
            return "/customer/update-customer";
        }

        if (!file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            customer.setPhotoUrl(fileName);
            String uploadDir = "src/main/resources/static/images/";
            Path uploadPath = Paths.get(uploadDir, fileName);

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, uploadPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                model.addAttribute("errorMessage", "File saving error: " + ex.getMessage());
                return "/customer/update-customer";
            }
        }

        customerRepository.save(customer);
        return "redirect:/customers";
    }
    @PreAuthorize("hasRole('ROLE_admin')")
    @GetMapping("customers/delete/{id}")
    public String deleteCustomerForm(@PathVariable("id") int id, Model model) {
        Optional<Customer> customer = customerRepository.findById(id);
        if (customer.isPresent()) {
            model.addAttribute("customer", customer.get());
            return "/customer/delete-screen-customer";
        } else {
            return "redirect:/customers";
        }
    }

    @PreAuthorize("hasRole('ROLE_admin')")
    @PostMapping("customers/delete/{id}")
    public String deleteCustomer(@PathVariable("id") int id) {
        customerRepository.deleteById(id);
        return "redirect:/customers";
    }

    @GetMapping("/customers/search")
    public String searchName(@RequestParam(value = "query") String query, Model model) {
        List<Customer> customers = customerRepository.searchName(query);
        model.addAttribute("customers", customers);
        return "/customer/customers-list";
    }
}
