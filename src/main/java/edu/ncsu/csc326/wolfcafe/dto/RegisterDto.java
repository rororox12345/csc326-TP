package edu.ncsu.csc326.wolfcafe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Information needed to register a new customer.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {

    /** User name */
    private String name;

    /** User username */
    private String username;

    /** User email */
    private String email;

    /** User password */
    private String password;
}
