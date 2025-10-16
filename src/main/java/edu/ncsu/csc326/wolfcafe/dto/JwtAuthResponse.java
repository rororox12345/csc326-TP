package edu.ncsu.csc326.wolfcafe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response for authenticated and authorized user.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthResponse {

    /** The access token */
    private String accessToken;

    /** The Token type */
    private String tokenType = "Bearer";

    /** The Role of the User */
    private String role;
}
