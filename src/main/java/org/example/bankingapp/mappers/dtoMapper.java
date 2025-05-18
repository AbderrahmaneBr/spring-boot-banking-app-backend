package org.example.bankingapp.mappers;

import org.example.bankingapp.domain.entities.AccountOperation;
import org.example.bankingapp.domain.entities.CurrentAccount;
import org.example.bankingapp.domain.entities.Customer;
import org.example.bankingapp.domain.entities.SavingAccount;
import org.example.bankingapp.dto.AccountOperationDTO;
import org.example.bankingapp.dto.CurrentAccountDTO;
import org.example.bankingapp.dto.CustomerDTO;
import org.example.bankingapp.dto.SavingAccountDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class dtoMapper {
    public CustomerDTO fromCustomer(Customer customer) {
        CustomerDTO customerDTO = new CustomerDTO();
        BeanUtils.copyProperties(customer, customerDTO);
        return customerDTO;
    }

    public Customer fromCustomerDTO(CustomerDTO customerDTO) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(customerDTO, customer);
        return customer;
    }

    public SavingAccount fromSavingAccountDTO(SavingAccountDTO savingAccountDTO) {
        SavingAccount savingAccount = new SavingAccount();
        BeanUtils.copyProperties(savingAccountDTO, savingAccount);
        savingAccount.setCustomer(fromCustomerDTO(savingAccountDTO.getCustomer()));
        return savingAccount;
    }

    public SavingAccountDTO fromSavingAccount(SavingAccount savingAccount) {
        SavingAccountDTO savingAccountDTO = new SavingAccountDTO();
        BeanUtils.copyProperties(savingAccount, savingAccountDTO);
        savingAccountDTO.setCustomer(fromCustomer(savingAccount.getCustomer()));
        return savingAccountDTO;
    }

    public CurrentAccount fromCurrentAccountDTO(CurrentAccountDTO currentAccountDTO) {
        CurrentAccount currentAccount = new CurrentAccount();
        BeanUtils.copyProperties(currentAccountDTO, currentAccount);
        currentAccount.setCustomer(fromCustomerDTO(currentAccountDTO.getCustomer()));
        return currentAccount;
    }

    public CurrentAccountDTO fromCurrentAccount(CurrentAccount currentAccount) {
        CurrentAccountDTO currentAccountDTO = new CurrentAccountDTO();
        BeanUtils.copyProperties(currentAccount, currentAccountDTO);
        currentAccountDTO.setCustomer(fromCustomer(currentAccount.getCustomer()));
        return currentAccountDTO;
    }

    public AccountOperationDTO fromAccountOperation(AccountOperation accountOperation) {
        AccountOperationDTO accountOperationDTO = new AccountOperationDTO();
        BeanUtils.copyProperties(accountOperation, accountOperationDTO);
        return accountOperationDTO;
    }

    public AccountOperation fromAccountOperationDTO(AccountOperationDTO accountOperationDTO) {
        AccountOperation accountOperation = new AccountOperation();
        BeanUtils.copyProperties(accountOperationDTO, accountOperation);
        return accountOperation;
    }

}
