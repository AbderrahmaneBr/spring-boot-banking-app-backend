package org.example.bankingapp.exceptions;

public class BankAccountNotFoundException extends RuntimeException {
    public BankAccountNotFoundException() {
        super("Bank account not found");
    }
}
