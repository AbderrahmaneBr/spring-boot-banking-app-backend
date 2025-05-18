package org.example.bankingapp.domain.repositories;

import org.example.bankingapp.domain.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount, String> {
}
