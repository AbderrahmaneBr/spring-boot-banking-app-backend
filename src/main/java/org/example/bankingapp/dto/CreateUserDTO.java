package org.example.bankingapp.dto;

import lombok.Data;
import org.springframework.stereotype.Service;

@Data
public class CreateUserDTO {
    private String username;
    private String password;
    private java.util.List<String> roles = java.util.Arrays.asList("USER");
}
