package org.example.bankingapp.services.impl;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.bankingapp.domain.entities.*;
import org.example.bankingapp.domain.enums.OperationType;
import org.example.bankingapp.domain.repositories.AccountOperationRepository;
import org.example.bankingapp.domain.repositories.BankAccountRepository;
import org.example.bankingapp.domain.repositories.CustomerRepository;
import org.example.bankingapp.dto.*;
import org.example.bankingapp.exceptions.BankAccountNotFoundException;
import org.example.bankingapp.exceptions.CustomerNotFoundException;
import org.example.bankingapp.mappers.dtoMapper;
import org.example.bankingapp.services.BankAccountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {
    private final CustomerRepository customerRepository;
    private final BankAccountRepository bankAccountRepository;
    private final dtoMapper dtoMapper;
    private final AccountOperationRepository accountOperationRepository;

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        Customer savedCustomer = customerRepository.save(dtoMapper.fromCustomerDTO(customerDTO));
        return dtoMapper.fromCustomer(savedCustomer);
    }

    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerDTO.getId()).orElseThrow(CustomerNotFoundException::new);
        Customer savedCustomer = customerRepository.save(dtoMapper.fromCustomerDTO(customerDTO));
        return dtoMapper.fromCustomer(savedCustomer);
    }

    @Override
    public CurrentAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(CustomerNotFoundException::new);
        CurrentAccount currentAccount = new CurrentAccount();
        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setBalance(initialBalance);
        currentAccount.setOverDraft(overDraft);
        currentAccount.setCustomer(customer);
        currentAccount.setCreatedAt(new Date());
        CurrentAccount savedCurrentAccount = bankAccountRepository.save(currentAccount);
        return dtoMapper.fromCurrentAccount(savedCurrentAccount);
    }

    @Override
    public SavingAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElseThrow(CustomerNotFoundException::new);
        SavingAccount savingAccount = new SavingAccount();
        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setBalance(initialBalance);
        savingAccount.setInterestRate(interestRate);
        savingAccount.setCustomer(customer);
        savingAccount.setCreatedAt(new Date());
        SavingAccount savedBankAccount = bankAccountRepository.save(savingAccount);
        return dtoMapper.fromSavingAccount(savedBankAccount);
    }

    @Override
    public List<CustomerDTO> listCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream().map(dtoMapper::fromCustomer).collect(Collectors.toList());

    }

    @Override
    public CustomerDTO getCustomerById(Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElseThrow(CustomerNotFoundException::new);
        return dtoMapper.fromCustomer(customer);
    }

    @Override
    public BankAccountDTO getBankAccount(String bankAccountId) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(bankAccountId).orElseThrow(BankAccountNotFoundException::new);
        if(bankAccount instanceof SavingAccount) {
            SavingAccount savingAccount = (SavingAccount) bankAccount;
            return dtoMapper.fromSavingAccount(savingAccount);
        } else {
            CurrentAccount currentAccount = (CurrentAccount) bankAccount;
            return dtoMapper.fromCurrentAccount(currentAccount);
        }
    }

    @Override
    public void debit(String bankAccountId, double amount, String description) {
        BankAccount bankAccount = bankAccountRepository.findById(bankAccountId).orElseThrow(BankAccountNotFoundException::new);
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setBankAccount(bankAccount);
        accountOperation.setDescription(description);
        accountOperation.setType(OperationType.DEBIT);
        accountOperation.setOperationDate(new Date());
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance() - amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void credit(String bankAccountId, double amount, String description) {
        BankAccount bankAccount = bankAccountRepository.findById(bankAccountId).orElseThrow(BankAccountNotFoundException::new);
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setBankAccount(bankAccount);
        accountOperation.setDescription(description);
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setOperationDate(new Date());
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance() + amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void transfer(String fromBankAccountId, String toBankAccountId, double amount) {
        debit(fromBankAccountId, amount, "Transfer to " + toBankAccountId);
        credit(fromBankAccountId, amount, "Transfer from " + fromBankAccountId);
    }

    @Override
    public List<BankAccountDTO> listBankAccounts() {
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        return bankAccounts.stream().map(bankAccount -> {
            if(bankAccount instanceof SavingAccount) {
                SavingAccount savingAccount = (SavingAccount) bankAccount;
                return dtoMapper.fromSavingAccount(savingAccount);
            } else if (bankAccount instanceof CurrentAccount) {
                CurrentAccount currentAccount = (CurrentAccount) bankAccount;
                return dtoMapper.fromCurrentAccount(currentAccount);
            }
            return null;
        }).collect(Collectors.toList());

    }

    @Override
    public void deleteCustomer(Long id) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(id).orElseThrow(CustomerNotFoundException::new);
        customerRepository.delete(customer);
    }

    @Override
    public List<AccountOperationDTO> accountHistory(String accountId) {
        List<AccountOperation> accountOperations = accountOperationRepository.findByBankAccount_Id(accountId);
        List<AccountOperationDTO> accountOperationDTOS = accountOperations.stream().map(dtoMapper::fromAccountOperation).collect(Collectors.toList());
        return accountOperationDTOS;
    }

    @Override
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException {

        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElseThrow(BankAccountNotFoundException::new);
        Page<AccountOperation> accountOperations = accountOperationRepository.findByBankAccount_Id(accountId, PageRequest.of(page, size));
        AccountHistoryDTO accountHistoryDTO = new AccountHistoryDTO();
        accountHistoryDTO.setAccountOperationsDTOS(accountOperations.getContent().stream().map(dtoMapper::fromAccountOperation).collect(Collectors.toList()));
        accountHistoryDTO.setAccountId(accountId);
        accountHistoryDTO.setBalance(bankAccount.getBalance());
        accountHistoryDTO.setPageSize(size);
        accountHistoryDTO.setCurrentPage(page);
        accountHistoryDTO.setTotalPage(accountOperations.getTotalPages());
        return accountHistoryDTO;
    }
}
