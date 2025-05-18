package org.example.bankingapp.domain.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("SA")
@Data @NoArgsConstructor
@AllArgsConstructor
public class SavingAccount extends BankAccount {
    private double interestRate;
}
