package org.example.bankingapp.domain.repositories;

import org.example.bankingapp.domain.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
