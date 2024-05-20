package com.sau.bankingmanagementgradle.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "account")
public class Account {
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull(message = "Id should not be null")
    @Min(value = 1, message = "Id should be bigger than 0")
    private int id;

    @NotEmpty(message = "Branch should not be empty.")
    @Size(max = 16, message = "Branch name should not exceed 16 characters!")
    @Column(name = "accountBranch", length = 16)
    private String branch;

    @NotNull(message = "Balance should not be null")
    @Min(value = 1, message = "Balance should be bigger than 0")
    @Column(name = "accountBalance")
    private BigDecimal balance;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Withdrawal> withdrawals;
}
