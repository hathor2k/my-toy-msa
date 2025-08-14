package com.example.userservice.vo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestUser {
    @NotNull(message = "Email can not be null")
    @Size(min = 2, message = "Email not be less than tow characters.")
    @Email
    private String email;

    @NotNull(message = "Password can not be null")
    @Size(min = 8, message = "Password must be equal or greater than 8 characters.")
    private String pwd;

    @NotNull(message = "Name can not be null")
    @Size(min = 2, message = "Name not be less than tow characters.")
    private String name;
}
