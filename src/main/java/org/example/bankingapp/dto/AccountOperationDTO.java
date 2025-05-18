package org.example.bankingapp.dto;

import lombok.Data;
import org.example.bankingapp.domain.enums.OperationType;

import java.util.Date;

@Data
public class AccountOperationDTO {
    private Long id;
    private Date operationDate;
    private double amount;
    private OperationType type;
    private String description;
}
