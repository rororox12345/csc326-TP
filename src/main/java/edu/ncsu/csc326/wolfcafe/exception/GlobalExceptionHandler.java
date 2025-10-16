package edu.ncsu.csc326.wolfcafe.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * Handles global errors.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * TODO
     *
     * @param exception
     *            TODO
     * @param webRequest
     *            TODO
     * @return TODO
     */
    @ExceptionHandler ( WolfCafeAPIException.class )
    public ResponseEntity<ErrorDetails> handleAPIException ( final WolfCafeAPIException exception,
            final WebRequest webRequest ) {
        final ErrorDetails errorDetails = new ErrorDetails( LocalDateTime.now(), exception.getMessage(),
                webRequest.getDescription( false ) );

        return new ResponseEntity<>( errorDetails, HttpStatus.BAD_REQUEST );
    }
}
