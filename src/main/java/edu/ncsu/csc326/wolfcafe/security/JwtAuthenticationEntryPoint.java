package edu.ncsu.csc326.wolfcafe.security;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Point of entry for Spring Security that looks to see if the user is
 * authenticated.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Checks that the user is authenticated. Sends an error if unauthorized.
     */
    @Override
    public void commence ( final HttpServletRequest request, final HttpServletResponse response,
            final AuthenticationException authException ) throws IOException, ServletException {
        response.sendError( HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage() );
    }
}
