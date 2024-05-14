package com.sau.bankingmanagementgradle.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WithdrawalView {

    private int id;
    private int customerId;
    private int accountId;
    private String customerName;
    private String accountBranch;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime date;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime updatedDate;

    private BigDecimal amount;

}
