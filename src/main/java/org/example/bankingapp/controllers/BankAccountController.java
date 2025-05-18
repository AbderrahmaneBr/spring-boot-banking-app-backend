package org.example.bankingapp.controllers;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.bankingapp.domain.entities.BankAccount;
import org.example.bankingapp.dto.AccountHistoryDTO;
import org.example.bankingapp.dto.AccountOperationDTO;
import org.example.bankingapp.dto.BankAccountDTO;
import org.example.bankingapp.services.BankAccountService;
import org.example.bankingapp.services.impl.BankAccountServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BankAccountController {
    private final BankAccountService bankAccountService;

    @GetMapping("/accounts/{accountId}")
    public BankAccountDTO getBankAccount(@PathVariable String accountId) {
        return bankAccountService.getBankAccount(accountId);
    }

    @GetMapping("/accounts")
    public List<BankAccountDTO> getBankAccounts() {
        return bankAccountService.listBankAccounts();
    }

    @GetMapping("/accounts/{accountId}/operations")
    public List<AccountOperationDTO> getBankAccountHistory(@PathVariable String accountId) {
        return bankAccountService.accountHistory(accountId);
    }

    @GetMapping("/accounts/{accountId}/pageOperations")
    public AccountHistoryDTO getBankAccountHistoryPage(@PathVariable String accountId, @RequestParam(name="page", defaultValue = "0") int page, @RequestParam(name = "size", defaultValue = "5") int size) {
        return bankAccountService.getAccountHistory(accountId, page, size);
    }

}
