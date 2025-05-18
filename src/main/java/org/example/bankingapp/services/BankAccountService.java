package org.example.bankingapp.services;

import org.example.bankingapp.dto.*;
import org.example.bankingapp.exceptions.BankAccountNotFoundException;
import org.example.bankingapp.exceptions.CustomerNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

public interface BankAccountService {
    CustomerDTO saveCustomer(CustomerDTO customerDTO);
    CustomerDTO updateCustomer(CustomerDTO customerDTO) throws CustomerNotFoundException;
    CurrentAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException;
    SavingAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId);
    List<CustomerDTO> listCustomers();
    CustomerDTO getCustomerById(Long customerId) throws CustomerNotFoundException;
    BankAccountDTO getBankAccount(String bankAccountId) throws BankAccountNotFoundException;
    void debit(String bankAccountId, double amount, String description);
    void credit(String bankAccountId, double amount, String description);
    void transfer(String fromBankAccountId, String toBankAccountId, double amount);
    List<BankAccountDTO> listBankAccounts();

    void deleteCustomer(Long id) throws CustomerNotFoundException;

    List<AccountOperationDTO> accountHistory(String accountId);

    AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException;

    List<CustomerDTO> searchCustomers(String searchTerm);

    AccountOperationDTO saveOperation(String accountId, AccountOperationDTO accountOperationDTO);
}
