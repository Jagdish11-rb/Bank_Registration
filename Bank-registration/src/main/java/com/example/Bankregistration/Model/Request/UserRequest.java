package com.example.Bankregistration.Model.Request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {
    private String name;
    private String password;
    @Size(min=10,max=10)
    private String mobileNumber;
    private String accountNumber;
    private String accountType;
    private String accountIfsc;
    private String bankName;
    private String vpa;
    private String state;
    private String city;
    private String pin_code;
    private String pan_number;
    private String email;
    private String DOB;
}
