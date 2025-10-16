package edu.ncsu.csc326.wolfcafe.dto;

import edu.ncsu.csc326.wolfcafe.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Information to login a user.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    /** User id */
    private Long   id;

    /** User name */
    private String name;

    /** User username */
    private String username;

    /** User email */
    private String email;

    /** User role */
    private Role   role;

}
