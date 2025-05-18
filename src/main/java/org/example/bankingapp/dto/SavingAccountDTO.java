package org.example.bankingapp.dto;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.bankingapp.domain.enums.AccountStatus;

import java.util.Date;

@Data
public class SavingAccountDTO extends BankAccountDTO {
    private String id;
    private double balance;
    private Date createdAt;
    private AccountStatus status;
    private CustomerDTO customer;
    private double interestRate;
}
