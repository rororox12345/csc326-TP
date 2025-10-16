package edu.ncsu.csc326.wolfcafe.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Exception for WolfCafe API calls.
 */
@Getter
@AllArgsConstructor
public class WolfCafeAPIException extends RuntimeException {
    /** TODO */
    private static final long serialVersionUID = 1L;
    /** TODO */
    private final HttpStatus  status;
    /** TODO */
    private final String      message;
}
