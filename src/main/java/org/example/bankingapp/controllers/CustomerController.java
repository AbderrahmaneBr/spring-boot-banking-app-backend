package org.example.bankingapp.controllers;

import lombok.AllArgsConstructor;
import org.example.bankingapp.dto.CustomerDTO;
import org.example.bankingapp.exceptions.CustomerNotFoundException;
import org.example.bankingapp.services.BankAccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class CustomerController {
    private final BankAccountService bankAccountService;

    @GetMapping("/customers")
    public List<CustomerDTO> getCustomers() {
        return bankAccountService.listCustomers();
    }

    @GetMapping("/customers/{id}")
    public CustomerDTO getCustomer(@PathVariable(name="id") Long id) throws CustomerNotFoundException {
        return bankAccountService.getCustomerById(id);
    }

    @PostMapping("/customers")
    public CustomerDTO createCustomer(@RequestBody CustomerDTO customerDTO) {
        return bankAccountService.saveCustomer(customerDTO);
    }

    @PatchMapping("/customers/{id}")
    public CustomerDTO updateCustomer(@PathVariable(name="id") Long id, @RequestBody CustomerDTO customerDTO) throws CustomerNotFoundException {
        customerDTO.setId(id);
        return bankAccountService.updateCustomer(customerDTO);
    }

    @DeleteMapping("/customers/{id}")
    public void deleteCustomer(@PathVariable(name="id") Long id) throws CustomerNotFoundException {
        bankAccountService.deleteCustomer(id);
    }

}
