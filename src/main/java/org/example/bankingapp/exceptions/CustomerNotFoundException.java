package org.example.bankingapp.exceptions;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException() {
        super("Customer not found");
    }
}
